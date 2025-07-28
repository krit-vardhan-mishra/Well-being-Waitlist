import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useEffect } from 'react';
import planeImage from '../assets/images/plane-background.jpg';
import logoHeaderImage from '../assets/images/logo-header.png';
import logoFooterImage from '../assets/images/logo-footer.png';
import { usePageTitle } from '../hooks/usePageTitle';

const HomePage = () => {
  usePageTitle("Well-being Waitlist");

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const fromAdmin = urlParams.get('fromAdmin');
    
    if (!fromAdmin) {
      sessionStorage.removeItem('isAdmin');
      sessionStorage.removeItem('authToken');
    }
  }, []);

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.2
      }
    }
  };

  const item = {
    hidden: { opacity: 0, y: 30 },
    show: { opacity: 1, y: 0 }
  };

  return (
    <motion.div
      className="min-h-screen bg-cover bg-center bg-no-repeat pt-16 relative"
      style={{ backgroundImage: `url(${planeImage})` }}
      variants={container}
      initial="hidden"
      animate="show"
    >
      {/* Gradient Overlay */}
      <div className="absolute inset-0 bg-gradient-to-r from-indigo-500/60 to-purple-600/60 backdrop-blur-md z-0" />

      {/* Logos */}
      <motion.div className="relative z-10 text-center mt-8" variants={item}>
        <img src={logoHeaderImage} alt="Wellbeing-Waitlist Logo-Header" className="w-36 mx-auto mb-2" />
        <img src={logoFooterImage} alt="Wellbeing-Waitlist Logo-Footer" className="w-64 mx-auto" />
      </motion.div>

      {/* Content Box */}
      <motion.div
        className="max-w-xl mx-auto mt-10 bg-white bg-opacity-90 rounded-2xl shadow-2xl p-8 backdrop-blur-lg z-10 relative"
        variants={item}
      >
        <h1 className="text-4xl font-bold text-gray-800 underline mb-4 text-center">
          Wellbeing-Waitlist
        </h1>
        <p className="text-lg leading-relaxed mb-8 text-gray-800 text-center">
          The <strong>Wellbeing-Waitlist</strong> portal streamlines patient management by prioritizing urgent medical
          cases. Using AI to assess symptoms, it ensures critical patients are attended to promptly, improving workflow
          and outcomes for hospitals and patients alike.
        </p>

        {/* Animated Buttons */}
        <motion.div
          className="flex justify-center gap-6 mt-6"
          variants={item}
        >
          <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
            <Link
              to="/register"
              className="bg-indigo-600 hover:bg-indigo-700 text-white font-medium px-6 py-2 rounded-full transition duration-300"
            >
              Register Yourself
            </Link>
          </motion.div>

          <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
            <Link
              to="/details"
              className="bg-gray-800 hover:bg-gray-700 text-white font-medium px-6 py-2 rounded-full transition duration-300"
            >
              Check Details
            </Link>
          </motion.div>
        </motion.div>

        {/* Info about admin access */}
        <motion.div 
          className="mt-6 text-center text-sm text-gray-600"
          variants={item}
        >
          <p>For admin access, click "Check Details" then "Admin Access Only"</p>
        </motion.div>
      </motion.div>
    </motion.div>
  );
};

export default HomePage;