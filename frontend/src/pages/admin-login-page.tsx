import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import planeImage from '../assets/images/plane-background.jpg';
import logoHeaderImage from '../assets/images/logo-header.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { usePageTitle } from '../hooks/usePageTitle';
import { patientAPI, apiHelpers } from '../api/axios-setup';
import { AxiosError } from 'axios';

const AdminLoginPage = () => {
  usePageTitle("Admin Login Page");

  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const queryParams = new URLSearchParams(window.location.search);
    const urlErrorMessage = queryParams.get('errorMessage');
    if (urlErrorMessage) {
      setErrorMessage(urlErrorMessage);
    }
  }, []);

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
    if (errorMessage) setErrorMessage(''); // Clear error when user types
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMessage('');
    
    try {
      const response = await patientAPI.adminLogin(password);
      if (response.status === 200) {
        sessionStorage.setItem('isAdmin', 'true');
        sessionStorage.setItem('authToken', 'admin-authenticated'); // Optional token
        // Navigate to details page with success message and admin flag
        navigate('/details?message=Admin login successful - Full access granted&type=success');
      }
    } catch (error) {
      const axiosError = error as AxiosError;
      setErrorMessage(apiHelpers.handleError(axiosError));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      className="min-h-screen bg-cover bg-center bg-no-repeat pt-16 relative"
      style={{ backgroundImage: `url(${planeImage})` }}
    >
      {/* Background overlay */}
      <div className="absolute inset-0 bg-gradient-to-r from-indigo-500/60 to-purple-600/60 backdrop-blur-md z-0" />

      {/* Header logo */}
      <motion.div
        className="relative z-10 text-center mt-8"
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
      >
        <img src={logoHeaderImage} alt="Header Logo" className="w-36 mx-auto mb-4" />
      </motion.div>

      {/* Login box */}
      <motion.div
        className="max-w-xl mx-auto mt-10 bg-white bg-opacity-90 rounded-2xl shadow-2xl p-8 backdrop-blur-lg z-10 relative"
        initial={{ opacity: 0, y: 50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4, duration: 0.8 }}
      >
        <motion.h2
          className="text-3xl font-semibold text-gray-800 mb-6 text-center"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6 }}
        >
          Admin Login
        </motion.h2>

        {errorMessage && (
          <motion.p
            className="text-red-600 bg-red-50 p-3 rounded-lg text-center mb-4"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.7 }}
          >
            {errorMessage}
          </motion.p>
        )}

        <form onSubmit={handleSubmit} className="space-y-6 text-left">
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.8 }}
          >
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
              Enter Admin Password
            </label>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                id="password"
                name="password"
                placeholder="Enter the admin password"
                required
                value={password}
                onChange={handlePasswordChange}
                disabled={isLoading}
                className="w-full px-4 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:bg-gray-100"
              />
              <button
                type="button"
                onClick={togglePasswordVisibility}
                className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-500 focus:outline-none"
                disabled={isLoading}
              >
                <FontAwesomeIcon icon={showPassword ? faEyeSlash : faEye} className="h-5 w-5" />
              </button>
            </div>
          </motion.div>

          <motion.div
            className="flex justify-center gap-6 mt-4"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.9 }}
          >
            <button
              type="submit"
              disabled={isLoading}
              className="bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white font-medium px-6 py-2 rounded-full transition duration-300"
            >
              {isLoading ? 'Logging in...' : 'Login'}
            </button>
          </motion.div>
        </form>

        {/* Back to home link */}
        <motion.div
          className="flex flex-col items-center mt-8 gap-2"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.0 }}
        >
          <p className="text-blue-700 font-medium hover:text-black hover:underline">
            <Link to="/">Return to home page</Link>
          </p>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default AdminLoginPage;