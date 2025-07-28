import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import planeImage from '../assets/images/plane-background.jpg';
import logoHeaderImage from '../assets/images/logo-header.png';
import logoFooterImage from '../assets/images/logo-footer.png';
import { usePageTitle } from '../hooks/usePageTitle';
import { patientAPI, apiHelpers } from '../api/axios-setup';
import { AxiosError } from 'axios';

const RegisterPage = () => {
  usePageTitle("Register Patient");

  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    age: '',
    gender: 'none',
    problem: ''
  });
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState<'success' | 'error'>('error');

  useEffect(() => {
    const queryParams = new URLSearchParams(window.location.search);
    const urlMessage = queryParams.get('message');
    if (urlMessage) {
      setMessage(urlMessage);
      setMessageType('error');
    }
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    if (message) {
      setMessage('');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    if (formData.gender === 'none') {
      setMessage('Please select a gender');
      setMessageType('error');
      setLoading(false);
      return;
    }

    if (parseInt(formData.age) <= 0 || parseInt(formData.age) > 150) {
      setMessage('Please enter a valid age between 1 and 150');
      setMessageType('error');
      setLoading(false);
      return;
    }

    try {
      const response = await patientAPI.registerPatient(formData);
      
      if (response.status === 201) {
        setMessage('Registration successful! Redirecting to patient list...');
        setMessageType('success');
        setFormData({ name: '', age: '', gender: 'none', problem: '' });

        setTimeout(() => {
          navigate('/details?message=Patient registered successfully&type=success');
        }, 1500);
      }
    } catch (error) {
      const axiosError = error as AxiosError;
      const errorMessage = apiHelpers.handleError(axiosError);
      setMessage(errorMessage);
      setMessageType('error');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setFormData({ name: '', age: '', gender: 'none', problem: '' });
    setMessage('');
  };

  return (
    <div className="min-h-screen bg-cover bg-center bg-no-repeat pt-16 relative" style={{ backgroundImage: `url(${planeImage})` }}>
      <div className="absolute inset-0 bg-gradient-to-r from-indigo-500/60 to-purple-600/60 backdrop-blur-md z-0" />

      {/* Animated Logos */}
      <motion.div
        className="relative z-10 text-center mt-8"
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
      >
        <img src={logoHeaderImage} alt="Header Logo" className="w-36 mx-auto mb-2" />
        <img src={logoFooterImage} alt="Footer Logo" className="w-64 mx-auto" />
      </motion.div>

      {/* Form Card */}
      <motion.div
        className="text-center max-w-xl mx-auto mt-10 bg-white bg-opacity-90 rounded-2xl shadow-2xl p-8 backdrop-blur-lg z-10 relative"
        initial={{ opacity: 0, y: 50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4, duration: 0.8 }}
      >
        <motion.h2
          className="text-3xl font-semibold text-gray-800 mb-6"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6 }}
        >
          Register Patient
        </motion.h2>

        {message && (
          <motion.div 
            className={`p-4 rounded-lg text-center mb-6 ${
              messageType === 'success' 
                ? 'text-green-600 bg-green-50 border border-green-200' 
                : 'text-red-600 bg-red-50 border border-red-200'
            }`}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.7 }}
          >
            {message}
          </motion.div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6 text-left">
          <motion.div
            className="flex gap-4"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.7 }}
          >
            <div className="w-1/2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Name <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Your Name"
                required
                disabled={loading}
                minLength={2}
                maxLength={50}
                className="w-full px-4 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:bg-gray-100"
              />
            </div>
            <div className="w-1/2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Age <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                name="age"
                value={formData.age}
                onChange={handleChange}
                placeholder="Your Age"
                required
                disabled={loading}
                min="1"
                max="150"
                className="w-full px-4 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:bg-gray-100"
              />
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.8 }}
          >
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Gender <span className="text-red-500">*</span>
            </label>
            <select
              name="gender"
              value={formData.gender}
              onChange={handleChange}
              required
              disabled={loading}
              className="w-full px-4 py-2 pr-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:bg-gray-100"
            >
              <option value="none" disabled>Select Gender</option>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
          </motion.div>

          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.9 }}
          >
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Problem Description <span className="text-red-500">*</span>
            </label>
            <textarea
              name="problem"
              value={formData.problem}
              onChange={handleChange}
              placeholder="Describe your symptoms or medical concern in detail..."
              required
              disabled={loading}
              rows={4}
              minLength={5}
              maxLength={200}
              className="w-full px-4 py-2 border rounded-lg shadow-sm resize-none focus:outline-none focus:ring-2 focus:ring-indigo-400 disabled:bg-gray-100"
            />
            <div className="text-xs text-gray-500 mt-1">
              {formData.problem.length}/200 characters
            </div>
          </motion.div>

          <motion.div
            className="flex justify-center gap-6 mt-6"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1.0 }}
          >
            <button
              type="submit"
              disabled={loading}
              className="bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white font-medium px-6 py-2 rounded-full transition duration-300 flex items-center gap-2"
            >
              {loading && (
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
              )}
              {loading ? 'Registering...' : 'Submit Registration'}
            </button>
            <button
              type="button"
              onClick={handleReset}
              disabled={loading}
              className="bg-red-600 hover:bg-red-700 disabled:bg-red-400 text-white font-medium px-6 py-2 rounded-full transition duration-300"
            >
              Reset Form
            </button>
          </motion.div>
        </form>

        <motion.div
          className="flex justify-center mt-6"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.1 }}
        >
          <Link 
            to="/" 
            className="text-blue-700 hover:underline font-medium transition-colors duration-200"
          >
            ← Back to Home
          </Link>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default RegisterPage;