import axios from 'axios';

// Resolve base URL safely:
// Supports full API URL (e.g., https://rpms-backend.onrender.com/api),
// host-only URL (e.g., https://rpms-backend.onrender.com),
// trailing slashes, or defaults to local dev server http://localhost:8080/api.
const getBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL;
  if (!envUrl || !envUrl.trim()) {
    return 'http://localhost:8080/api';
  }
  let cleaned = envUrl.trim().replace(/\/+$/, '');
  if (!cleaned.endsWith('/api')) {
    cleaned += '/api';
  }
  return cleaned;
};

const api = axios.create({
  baseURL: getBaseUrl(),
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('rpms_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear token and redirect to login if session expired
      if (window.location.pathname !== '/login') {
        localStorage.removeItem('rpms_token');
        localStorage.removeItem('rpms_user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
