import { defineStore } from 'pinia';
import api from '../services/api';

export const usePublicStore = defineStore('public', {
    state: () => ({
        creatorProfile: null,
        blocks: [],
        products: [],
        currentOrder: null,
        loading: false,
        error: null
    }),
    actions: {
        async fetchPublicProfile(username) {
            this.loading = true;
            this.error = null;
            try {
                const res = await api.get(`/public/creators/${username}`);
                this.creatorProfile = res.data;
                this.blocks = res.data.blocks || [];
                this.products = res.data.products || [];
                return res.data;
            } catch (err) {
                this.error = err.response?.data?.message || 'Creator not found';
                throw err;
            } finally {
                this.loading = false;
            }
        },
        async createOrder(payload) {
            try {
                const res = await api.post('/public/orders', payload);
                this.currentOrder = res.data;
                return res.data;
            } catch (err) {
                console.error('Checkout creation error:', err);
                throw err;
            }
        },
        async getOrderStatus(orderId) {
            try {
                const res = await api.get(`/public/orders/${orderId}/status`);
                return res.data;
            } catch (err) {
                console.error('Order status check error:', err);
                throw err;
            }
        },
        async simulatePayment(orderId) {
            try {
                const res = await api.post(`/webhooks/payment/simulate-success/${orderId}`);
                return res.data;
            } catch (err) {
                console.error('Simulate payment failed:', err);
                throw err;
            }
        }
    }
})