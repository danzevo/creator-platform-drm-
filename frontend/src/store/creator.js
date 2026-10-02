import { defineStore } from 'pinia';
import api from '../services/api';

export const useCreatorStore = defineStore('creator', {
    state: () => ({
        dashboard: null,
        blocks: [],
        products: [],
        orders: [],
        payouts: [],
        balance: null,
        loading: false,
        error: null
    }),
    actions: {
        async fetchDashboard() {
            try {
                const res = await api.get('/creator/dashboard');
                this.dashboard = res.data;

                return res.data;
            } catch (err) {
                console.error("Failed to fetch dashboard:", err);
                throw err;
            }
        },
        async updateProfile(payload) {
            try {
                const res = await api.put('/creator/profile', payload);
                if (this.dashboard) {
                    this.dashboard.displayName = payload.displayName;
                    this.dashboard.bio = payload.bio;
                    this.dashboard.avatarUrl = payload.avatarUrl;
                }
                return res.data;
            } catch (err) {
                console.error("Failed to update profile:", err);
                throw err;
            }
        },
        async updateTheme(payload) {
            try {
                const res = await api.put('/creator/theme', payload);
                if (this.dashboard) {
                    this.dashboard.themeConfigJson = JSON.stringify(payload);
                }
                return res.data;
            } catch (err) {
                console.error('Failed to update theme:', err);
                throw err;
            }
        },
        async fetchBlocks() {
            try {
                const res = await api.get("/creator/blocks");
                this.blocks = res.data;
                return res.data;
            } catch (err) {
                console.error("Failed to fetch blocks:", err);
                throw err;
            }
        },
        async createBlock(payload) {
            try {
                const res = await api.post('/creator/blocks', payload);
                this.blocks.push(res.data);
                return res.data;
            } catch (err) {
                console.error("Failed to create block:", err);
                throw err;
            }
        },
        async updateBlock(id, payload) {
            try {
                const res = await api.put(`/creator/blocks/${id}`, payload);
                const idx = this.blocks.findIndex(b => b.id === id);
                if (idx !== -1)
                    this.blocks[idx] = res.data;

                return res.data;
            } catch (err) {
                console.error("Failed to update block:", err);
                throw err;
            }
        },
        async deleteBlock(id) {
            try {
                await api.delete(`/creator/blocks/${id}`);
                this.blocks = this.blocks.filter(b => b.id !== id);
            } catch (err) {
                console.error('Failed to delete block:', err);
                throw err;
            }
        },
        async reorderBlocks(items) {
            try {
                await api.put('/creator/blocks/reorder', { items });
            } catch (err) {
                console.error('Failed to reorder blocks:', err);
                throw err;
            }
        },
        async fetchProducts() {
            try {
                const res = await api.get('/creator/products');
                this.products = res.data;
                return res.data;
            } catch (err) {
                console.error('Failed to fetch products:', err);
                throw err;
            }
        },
        async createProduct(formData) {
            try {
                const res = await api.post('/creator/products', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
                this.products.unshift(res.data);
                if (this.dashboard) {
                    this.dashboard.totalProductsCount = (this.dashboard.totalProductsCount || 0) + 1;
                }
                return res.data;
            } catch (err) {
                console.error('Failed to create product:', err);
                throw err;
            }
        },
        async deleteProduct(id) {
            try {
                await api.delete(`/creator/products/${id}`);
                this.products = this.products.filter(p => p.id !== id);
                if (this.dashboard && this.dashboard.totalProductsCount > 0) {
                    this.dashboard.totalProductsCount--;
                }
            } catch (err) {
                console.error('Failed to delete product:', err);
                throw err;
            }
        },
        async fetchOrders() {
            try {
                const res = await api.get('/creator/orders');
                this.orders = res.data;
                return res.data;
            } catch (err) {
                console.error('Failed to fetch order:', err);
                throw err;
            }
        },
        async fetchBalance() {
            try {
                const res = await api.get('/creator/balance');
                this.balance = res.data;
                return res.data;
            } catch (err) {
                console.error('Failed to fetch balance:', err);
                throw err;
            }
        },
        async fetchPayouts() {
            try {
                const res = await api.get('/creator/payouts');
                this.payouts = res.data;
                return res.data;
            } catch (err) {
                console.error('Failed to fetch payouts:', err);
                throw err;
            }
        },
        async requestPayout(payload) {
            try {
                const res = await api.post('/creator/payouts', payload);
                this.payouts.unshift(res.data);
                await this.fetchBalance();
                return res.data;
            } catch (err) {
                console.error('Failed to request payout:', err);
                throw err;
            }
        },
        async fetchAll() {
            this.loading = true;
            this.error = null;
            try {
                await Promise.allSettled([
                    this.fetchDashboard(),
                    this.fetchBlocks(),
                    this.fetchProducts(),
                    this.fetchOrders(),
                    this.fetchBalance(),
                    this.fetchPayouts()
                ]);
            } catch (err) {
                this.error = 'Failed to load creator data';
            } finally {
                this.loading = false;
            }
        }
    }
})