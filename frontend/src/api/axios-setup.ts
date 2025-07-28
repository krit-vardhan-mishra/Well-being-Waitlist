import axios, { AxiosInstance, AxiosError, AxiosResponse } from "axios";

// Debug logging
const isDevelopment = process.env.NODE_ENV !== "production";
const BASE_URL = isDevelopment 
  ? "http://localhost:8080/api/v1/patients" 
  : "/api/v1/patients";

if (isDevelopment) {
  console.log("🚀 API Base URL:", BASE_URL);
}

const api: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 15000, 
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("authToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    console.error("❌ Request interceptor error:", error);
    return Promise.reject(error);
  }
);

// Response interceptor with debugging
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error: AxiosError) => {
    if (isDevelopment) {
      console.error("❌ API Error:", {
        message: error.message,
        code: error.code,
        status: error.response?.status,
        url: error.config?.url,
        data: error.response?.data
      });
    }
    
    if (error.response?.status === 401) {
      sessionStorage.removeItem("isAdmin");
      sessionStorage.removeItem("authToken");
      
      if (!window.location.pathname.includes("login")) {
        window.location.href =
          "/admin-login?errorMessage=Session expired. Please login again.";
      }
    } else if (error.response?.status === 403) {
      console.error("Access forbidden");
    } else if (error.response && error.response.status >= 500) {
      console.error("Server error:", error.response?.data);
    } else if (error.code === "ECONNABORTED") {
      console.error("Request timeout - Backend server may not be running");
    } else if (!error.response) {
      console.error("Network error - Backend server is not accessible");
    }
    
    return Promise.reject(error);
  }
);

export const patientAPI = {
  testConnection: () => {
    return api.get("/patients").catch(error => {
      console.error("❌ Connection test failed:", error.message);
      throw error;
    });
  },

  registerPatient: (patientData: {
    name: string;
    age: string;
    gender: string;
    problem: string;
  }) => {
    return api.post("/register", patientData);
  },
  
  getPatients: (cured?: boolean) => {
    const params = cured !== undefined ? { cured } : {};
    return api.get("/patients", { params });
  },
  
  getPatientById: (id: number) => {
    return api.get(`/${id}`);
  },
  
  adminLogin: (password: string) => {
    return api.post("/admin-login", { password });
  },
  
  markPatientAsCured: (id: number) => {
    return api.put(`/${id}`);
  },
  
  deletePatient: (id: number) => {
    return api.delete(`/${id}`);
  }
};

export const apiHelpers = {
  get: <T = any>(url: string, params?: any): Promise<AxiosResponse<T>> =>
    api.get(url, { params }),
  post: <T = any>(url: string, data?: any): Promise<AxiosResponse<T>> =>
    api.post(url, data),
  put: <T = any>(url: string, data?: any): Promise<AxiosResponse<T>> =>
    api.put(url, data),
  delete: <T = any>(url: string): Promise<AxiosResponse<T>> => api.delete(url),
  
  handleError: (error: AxiosError) => {
    if (error.code === "ECONNABORTED") {
      return "Connection timeout. Please check if the backend server is running on http://localhost:8080";
    }
    
    if (error.code === "ERR_NETWORK") {
      return "Network error. Please ensure the backend server is running and accessible.";
    }
    
    if (error.response?.data) {
      const errorMessage =
        (error.response.data as any)?.message ||
        (error.response.data as any)?.error ||
        `Server error (${error.response.status})`;
      return errorMessage;
    } else if (error.request) {
      return "Unable to connect to server. Please check if the backend is running on http://localhost:8080";
    } else {
      return error.message || "An unexpected error occurred";
    }
  },
  
  isNetworkError: (error: AxiosError) => !error.response && error.request,
  isServerError: (error: AxiosError) =>
    error.response && error.response.status >= 500,
  isClientError: (error: AxiosError) =>
    error.response &&
    error.response.status >= 400 &&
    error.response.status < 500,
};

if (isDevelopment) {
  setTimeout(() => {
    patientAPI.testConnection()
      .then(() => console.log("✅ Backend connection successful"))
      .catch(() => console.log("❌ Backend connection failed - Make sure Spring Boot is running"));
  }, 1000);
}

export { api as axiosInstance };
export default api;