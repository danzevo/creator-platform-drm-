import { defineStore } from 'pinia';
import api from '../services/api';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        user: null,
        loading: false,
        error: null
    }),
    getters: {
        isAuthenticated: (state) => !!state.token
    },
    actions: {
        async login(email, password) {
            this.loading = true;
            this.error = null;
            try {
                const res = await api.post('/auth/login', { email, password });
                this.token = res.data.token;
                this.user = {
                    email: res.data.email,
                    role: res.data.role,
                    username: res.data.username,
                    displayName: res.data.displayName
                };
                localStorage.setItem('token', this.token);
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || 'Invalid email or password';
                throw err;
            } finally {
                this.loading = false;
            }
        },
        async register(payload) {
            this.loading = true;
            this.error = null;
            try {
                const res = await api.post('/auth/register', payload);
                this.token = res.data.token;
                this.user = {
                    email: res.data.email,
                    role: res.data.role,
                    username: res.data.username,
                    displayName: res.data.displayName
                };
                localStorage.setItem('token', this.token);
                return true;
            } catch (err) {
                this.error = err.response?.data?.message || 'Registration failed. Please try again.';
                throw err;
            } finally {
                this.loading = false;
            }
        },
        async fetchMe() {
            if (!this.token) return;
            try {
                const res = await api.get('/auth/me');
                this.user = res.data;
            } catch (err) {
                this.logout();
            }
        },
        logout() {
            this.token = null;
            this.user = null;
            localStorage.removeItem('token');
        }
    }
})