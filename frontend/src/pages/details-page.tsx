import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import Patient from '../types/patient';
import planeImage from '../assets/images/plane-background.jpg';
import logoHeaderImage from '../assets/images/logo-header.png';
import logoFooterImage from '../assets/images/logo-footer.png';
import { usePageTitle } from '../hooks/usePageTitle';
import { patientAPI, apiHelpers } from '../api/axios-setup';
import { AxiosError } from 'axios';

interface PatientWithTimer extends Patient {
  remainingTime: number;
  totalTime: number;
}

interface CureNotification {
  id: number;
  name: string;
  timestamp: number;
}

const DetailsPage = () => {
  usePageTitle("Patient Details");

  const [patients, setPatients] = useState<PatientWithTimer[]>([]);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);
  const [deleting, setDeleting] = useState<number | null>(null);
  const [cureNotifications, setCureNotifications] = useState<CureNotification[]>([]);

  const timerRef = useRef<NodeJS.Timeout | null>(null);

  const queryParams = new URLSearchParams(window.location.search);
  const isAdmin = sessionStorage.getItem('isAdmin') === 'true';
  const urlMessage = queryParams.get('message');

  useEffect(() => {
    if (urlMessage) {
      setMessage(urlMessage);

      const timer = setTimeout(() => {
        setMessage('');
      }, 5000); 

      return () => clearTimeout(timer);
    }
  }, [urlMessage]);


  const calculateCureTime = (emergencyLevel: number): number => {
    if (emergencyLevel >= 90) return 30;
    if (emergencyLevel >= 70) return 60; 
    if (emergencyLevel >= 50) return 120;
    return 180;
  };

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        const response = isAdmin
          ? await patientAPI.getPatients()
          : await patientAPI.getPatients(false);

        const patientsWithTimers: PatientWithTimer[] = response.data.map((patient: Patient) => {
          const totalTime = calculateCureTime(patient.emergencyLevel);
          const arrivalTime = new Date(patient.arrivalTime).getTime();
          const currentTime = Date.now();
          const elapsedTime = Math.floor((currentTime - arrivalTime) / 1000);
          const remainingTime = Math.max(0, totalTime - elapsedTime);

          return {
            ...patient,
            remainingTime,
            totalTime
          };
        });

        setPatients(patientsWithTimers);
      } catch (error) {
        const axiosError = error as AxiosError;
        setMessage(apiHelpers.handleError(axiosError));
      } finally {
        setLoading(false);
      }
    };

    fetchPatients();
  }, [isAdmin]);

  useEffect(() => {
    if (patients.length === 0) return;

    timerRef.current = setInterval(() => {
      setPatients(prevPatients => {
        const updatedPatients = prevPatients.map(patient => ({
          ...patient,
          remainingTime: Math.max(0, patient.remainingTime - 1)
        }));

        const patientsToBeCured = updatedPatients.filter(
          patient => patient.remainingTime === 0 && !patient.cured
        );

        patientsToBeCured.forEach(patient => {
          setCureNotifications(prev => [...prev, {
            id: patient.id,
            name: patient.name,
            timestamp: Date.now()
          }]);

          setTimeout(() => {
            setCureNotifications(prev =>
              prev.filter(notification => notification.id !== patient.id)
            );
          }, 3000);

          handleMarkAsCured(patient.id, false); 
        });

        return updatedPatients;
      });
    }, 1000);

    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, [patients.length]);

  const formatTime = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  };

  const getEmergencyLevelColor = (level: number) => {
    if (level >= 90) return 'bg-red-100 text-red-800';
    if (level >= 70) return 'bg-orange-100 text-orange-800';
    if (level >= 50) return 'bg-yellow-100 text-yellow-800';
    return 'bg-green-100 text-green-800';
  };

  const getEmergencyLevelText = (level: number) => {
    if (level >= 90) return 'Critical';
    if (level >= 70) return 'High';
    if (level >= 50) return 'Medium';
    return 'Low';
  };

  const getProgressBarColor = (remainingTime: number, totalTime: number) => {
    const percentage = (remainingTime / totalTime) * 100;
    if (percentage > 66) return 'bg-green-500';
    if (percentage > 33) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  const handleDeletePatient = async (patientId: number) => {
    setDeleting(patientId);
    try {
      await apiHelpers.delete(`/${patientId}`);
      setPatients(prev => prev.filter(p => p.id !== patientId));
      setMessage('Patient record deleted successfully');
      setDeleteConfirm(null);
    } catch (error) {
      const axiosError = error as AxiosError;
      setMessage(apiHelpers.handleError(axiosError));
    } finally {
      setDeleting(null);
    }
  };

  const handleMarkAsCured = async (patientId: number, showMessage: boolean = true) => {
    try {
      await apiHelpers.put(`/${patientId}`);

      if (!isAdmin) {
        setPatients(prev => prev.filter(p => p.id !== patientId));
      } else {
        setPatients(prev => prev.map(p =>
          p.id === patientId ? { ...p, cured: true } : p
        ));
      }

      if (showMessage) {
        setMessage('Patient marked as cured successfully');
      }
    } catch (error) {
      const axiosError = error as AxiosError;
      setMessage(apiHelpers.handleError(axiosError));
    }
  };

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.05,
        delayChildren: 0.2,
      },
    },
  };

  const item = {
    hidden: { opacity: 0, y: 10 },
    show: {
      opacity: 1,
      y: 0,
      transition: {
        duration: 0.3,
        ease: "easeInOut",
      },
    },
  };

  return (
    <div className="min-h-screen bg-cover bg-center bg-no-repeat pt-20 pb-12 relative"
      style={{ backgroundImage: `url(${planeImage})` }}>

      {/* Gradient Overlay */}
      <div className="absolute inset-0 bg-gradient-to-r from-indigo-600/70 to-purple-700/70 backdrop-blur-sm z-0" />

      {/* Cure Notifications */}
      <div className="fixed top-4 left-1/2 transform -translate-x-1/2 z-50 space-y-2">
        <AnimatePresence>
          {cureNotifications.map((notification) => (
            <motion.div
              key={notification.id}
              initial={{ opacity: 0, y: -50, scale: 0.8 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -50, scale: 0.8 }}
              className="bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg font-medium text-center min-w-80"
            >
              🎉 Patient cured successfully: {notification.name}
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {/* Logos */}
      <motion.div className="relative z-10 text-center mb-10" variants={container} initial="hidden" animate="show">
        <motion.img src={logoHeaderImage} alt="Header Logo" className="w-32 mx-auto mb-4" variants={item} />
        <motion.img src={logoFooterImage} alt="Footer Logo" className="w-56 mx-auto" variants={item} />
      </motion.div>

      {/* Main Content */}
      <motion.div className="max-w-7xl mx-auto bg-white/95 rounded-2xl shadow-xl p-8 z-10 relative"
        variants={container} initial="hidden" animate="show">

        <div className="flex flex-col items-center mb-8">
          <motion.h2
            className="text-3xl font-bold text-gray-900 tracking-tight"
            variants={item}
          >
            Patient Waiting List {isAdmin && '(Admin View)'}
          </motion.h2>

          {isAdmin && (
            <motion.div
              variants={item}
              className="mt-2 text-sm text-gray-600 bg-blue-50 px-3 py-1 rounded-full"
            >
              Admin Access: Full Control
            </motion.div>
          )}
        </div>


        {message && (
          <motion.p className={`${message.includes('success') ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50'} p-4 rounded-lg text-center mb-6`} variants={item}>
            {message}
          </motion.p>
        )}

        {loading ? (
          <motion.p className="text-center text-gray-600 text-lg" variants={item}>
            Loading patient data...
          </motion.p>
        ) : patients.length > 0 ? (
          <motion.div className="overflow-x-auto" variants={item}>
            <table className="w-full border-collapse bg-white rounded-lg overflow-hidden">
              <thead>
                <tr className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white">
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Queue</th>
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Name</th>
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Age</th>
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Gender</th>
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Problem</th>
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Priority</th>
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Time to Cure</th>
                  <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Progress</th>
                  {isAdmin && <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Status</th>}
                  {isAdmin && <th className="p-4 text-left font-semibold text-sm uppercase tracking-wider">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {patients
                  .filter(patient => !patient.cured || isAdmin) 
                  .sort((a, b) => {
                    if (a.emergencyLevel !== b.emergencyLevel) {
                      return b.emergencyLevel - a.emergencyLevel;
                    }
                    return a.remainingTime - b.remainingTime;
                  })
                  .map((patient, index) => (
                    <motion.tr
                      key={patient.id}
                      className={`border-b border-gray-200 ${patient.cured ? 'bg-green-50' :
                        patient.remainingTime === 0 ? 'bg-yellow-50' :
                          index % 2 === 0 ? 'bg-gray-50' : 'bg-white'
                        } hover:bg-indigo-50 transition-colors duration-200`}
                      variants={item}
                    >
                      <td className="p-4 text-gray-800 font-medium">#{index + 1}</td>
                      <td className="p-4 text-gray-800 font-medium">{patient.name}</td>
                      <td className="p-4 text-gray-800">{patient.age}</td>
                      <td className="p-4 text-gray-800">{patient.gender}</td>
                      <td className="p-4 text-gray-800 max-w-xs truncate" title={patient.problem}>
                        {patient.problem}
                      </td>
                      <td className="p-4">
                        <span className={`px-3 py-1 rounded-full text-sm font-medium ${getEmergencyLevelColor(patient.emergencyLevel)}`}>
                          {getEmergencyLevelText(patient.emergencyLevel)} ({patient.emergencyLevel})
                        </span>
                      </td>
                      <td className="p-4">
                        {patient.cured ? (
                          <span className="text-green-600 font-medium">Completed</span>
                        ) : patient.remainingTime === 0 ? (
                          <span className="text-red-600 font-medium animate-pulse">Curing Now...</span>
                        ) : (
                          <div className="text-center">
                            <div className="text-lg font-mono font-bold text-gray-800">
                              {formatTime(patient.remainingTime)}
                            </div>
                            <div className="text-xs text-gray-500">
                              of {formatTime(patient.totalTime)}
                            </div>
                          </div>
                        )}
                      </td>
                      <td className="p-4">
                        {patient.cured ? (
                          <div className="text-green-600 font-medium">✅ Cured</div>
                        ) : (
                          <div className="w-full bg-gray-200 rounded-full h-3">
                            <div
                              className={`h-3 rounded-full transition-all duration-1000 ${getProgressBarColor(patient.remainingTime, patient.totalTime)}`}
                              style={{
                                width: `${100 - (patient.remainingTime / patient.totalTime) * 100}%`
                              }}
                            ></div>
                          </div>
                        )}
                      </td>
                      {isAdmin && (
                        <td className="p-4">
                          <span className={`px-3 py-1 rounded-full text-sm font-medium ${patient.cured ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>
                            {patient.cured ? 'Cured' : 'In Treatment'}
                          </span>
                        </td>
                      )}
                      {isAdmin && (
                        <td className="p-4">
                          <div className="flex gap-2">
                            {!patient.cured && (
                              <button
                                onClick={() => handleMarkAsCured(patient.id)}
                                className="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded text-sm transition duration-200"
                              >
                                Mark Cured
                              </button>
                            )}
                            <button
                              onClick={() => setDeleteConfirm(patient.id)}
                              disabled={deleting === patient.id}
                              className="bg-red-600 hover:bg-red-700 disabled:bg-red-400 text-white px-3 py-1 rounded text-sm transition duration-200"
                            >
                              {deleting === patient.id ? 'Deleting...' : 'Delete'}
                            </button>
                          </div>
                        </td>
                      )}
                    </motion.tr>
                  ))}
              </tbody>
            </table>
          </motion.div>
        ) : (
          <motion.p className="text-center text-gray-600 text-lg" variants={item}>
            No patients found.
          </motion.p>
        )}

        <motion.div className="flex flex-col items-center mt-8 gap-4" variants={item}>
          {!isAdmin && (
            <Link
              to="/admin-login"
              className="text-indigo-600 hover:text-indigo-800 font-medium underline transition-colors duration-200"
            >
              Admin Access Only
            </Link>
          )}
          <Link
            to="/"
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-full font-medium transition-colors duration-300"
          >
            Back to Home
          </Link>
        </motion.div>
      </motion.div>

      {/* Delete Confirmation Modal */}
      {deleteConfirm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Confirm Delete</h3>
            <p className="text-gray-600 mb-6">
              Are you sure you want to delete this patient record? This action cannot be undone.
            </p>
            <div className="flex justify-end gap-3">
              <button
                onClick={() => setDeleteConfirm(null)}
                className="px-4 py-2 text-gray-600 border border-gray-300 rounded hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={() => handleDeletePatient(deleteConfirm)}
                disabled={deleting === deleteConfirm}
                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 disabled:bg-red-400"
              >
                {deleting === deleteConfirm ? 'Deleting...' : 'Delete'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DetailsPage;