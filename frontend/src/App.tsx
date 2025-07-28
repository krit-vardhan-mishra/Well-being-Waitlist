import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/home-page';
import RegisterPage from './pages/register-page';
import DetailsPage from './pages/details-page';
import AdminLoginPage from './pages/admin-login-page';

const AppContent = () => {

  return (
    <div className="min-h-screen">
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/details" element={<DetailsPage />} />
        <Route path="/admin-login" element={<AdminLoginPage />} />
      </Routes>
    </div>
  );
};

function App() {
  return (
      <Router>
        <AppContent />
      </Router>
  );
}

export default App;