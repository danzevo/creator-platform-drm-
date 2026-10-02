import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../store/auth';

import LandingPage from '../views/LandingPage.vue';
import AuthView from '../views/AuthView.vue';
import CreatorStudio from '../views/CreatorStudio.vue';
import BioStorefront from '../views/BioStorefront.vue';
import SecureReaderView from '../views/SecureReaderView.vue';

const routes = [
    {
        path: '/',
        name: 'Landing',
        component: LandingPage,
        meta: { title: 'LumenBio - Anti-Piracy Creator Storefront' }
    },
    {
        path: '/login',
        name: 'Login',
        component: AuthView,
        meta: { guestOnly: true, title: 'Sign In - LumenBio' }
    },
    {
        path: '/register',
        name: 'Register',
        component: AuthView,
        meta: { guestOnly: true, title: 'Create Account - LumenBio' }
    },
    {
        path: '/studio',
        name: 'CreatorStudio',
        component: CreatorStudio,
        meta: { requiresAuth: true, title: 'Creator Studio - LumenBio' }
    },
    {
        path: '/@:username',
        name: 'BioStorefront',
        component: BioStorefront,
        meta: { title: 'Creator Bio Storefront' }
    },
    {
        path: '/read/:orderId',
        name: 'SecureReader',
        component: SecureReaderView,
        meta: { title: 'Protected Document Reader' }
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior() {
        return { top: 0 };
    }
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.title) {
        document.title = to.meta.title;
    }

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ path: '/login', query: { redirect: to.fullPath } });
    } else if (to.meta.guestOnly && authStore.isAuthenticated) {
        next({ path: '/studio' });
    } else {
        next();
    }
});

export default router;