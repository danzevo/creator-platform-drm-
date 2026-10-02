import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

// Inject Bearer Token
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Intercept 401 Unauthorized
api.interceptors.response.use(
    (res) => res,
    (err) => {
        if (err.response?.status === 401) {
            localStorage.removeItem('token');

            // Only redirect if inside studio
            if (window.location.pathname.startsWith('/studio')) {
                window.location.href = '/login';
            }
        }
        return Promise.reject(err);
    }
);

export default api;