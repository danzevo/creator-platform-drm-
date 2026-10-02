<template>
    <nav class="sticky top-0 z-50 glass-nav transition-all duration-300">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex items-center justify-between h-16">
                <!-- Logo & Brand -->
                <router-link to="/" class="flex items-center space-x-3 group">
                    <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-sky-500 to-cyan-400 p-0.5 shadow-lg shadow-sky-500/20 group-hover:shadow-sky-500/40 transition-all duration-300">
                        <div class="w-full h-full bg-[#0b0f19] rounded-[10px] flex items-center justify-center">
                            <ShieldCheck class="w-5 h-5 text-sky-400 group-hover:scale-110 transition-transform duration-300" />
                        </div>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-lg font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-100 to-slate-400 bg-clip-text text-transparent">LumenBio</span>
                        <span class="text-[10px] text-sky-400/80 font-medium tracking-wide uppercase -mt-1">Anti-Piracy Suite</span>
                    </div>
                </router-link>
                <!-- Desktop Navigation Links -->
                <div class="hidden md:flex items-center space-x-8 text-sm font-medium text-slate-300">
                    <a href="#features" class="hover:text-sky-400 transition-colors">Features</a>
                    <a href="#anti-piracy" class="hover:text-sky-400 transition-colors flex items-center gap-1.5">
                        <span class="w-1.5 h-1.5 rounded-full bg-cyan-400 animate-pulse"></span>
                        Anti-Piracy DRM
                    </a>
                    <a href="#comparison" class="hover:text-sky-400 transition-colors">Why Us</a>
                    <a href="#pricing" class="hover:text-sky-400 transition-colors">Pricing</a>
                </div>
                <!-- Auth Action Buttons -->
                <div class="hidden md:flex items-center space-x-4">
                    <template v-if="authStore.isAuthenticated">
                        <router-link to="/studio" class="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 hover:opacity-95 shadow-md shadow-sky-500/20 hover:shadow-sky-500/40 transition-all duration-200">
                            <LayoutDashboard class="w-4 h-4" />
                            Creator Studio
                        </router-link>
                        <button @click="handleLogout"
                                title="Sign Out"
                                class="p-2 rounded-xl text-slate-400 hover:text-rose-400 hover:bg-slate-800/60 transition-colors">
                                <LogOut class="w-4 h-4" />
                        </button>
                    </template>
                    <template v-else>
                        <router-link to="/login" class="text-sm font-medium text-slate-300 hover:text-white px-3 py-2 transition-colors">
                            Sign In
                        </router-link>
                        <router-link to="/register" class="flex items-center gap-1.5 px-4 py-2 rounded-xl text-sm font-semibold bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 hover:opacity-95 shadow-md shadow-sky-500/20 hover:shadow-sky-500/40 transition-all duration-200">
                            Get Started Free
                            <ArrowRight class="w-4 h-4" />
                        </router-link>
                    </template>
                </div>
                <!-- Mobile Menu Toggle Button -->
                <div class="md:hidden flex items-center">
                    <button @click="mobileMenuOpen = !mobileMenuOpen" class="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 focus:outline-none">
                        <Menu v-if="!mobileMenuOpen" class="w-6 h-6" />
                        <X v-else class="w-6 h-6" />
                    </button>
                </div>
            </div>
        </div>
        <!-- Mobile Drawer -->
        <div v-show="mobileMenuOpen" class="md:hidden glass-card border-t border-slate-800 px-4 pt-5 pb-5 space-y-3">
            <a href="#features" @click="mobileMenuOpen = false" class="block py-2 text-slate-300 hover:text-sky-400 text-sm font-medium">
                Features
            </a>
            <a href="#anti-piracy" @click="mobileMenuOpen = false" class="block py-2 text-slate-300 hover:text-sky-400 text-sm font-medium">
                Anti-Piracy DRM
            </a>
            <a href="#comparison" @click="mobileMenuOpen = false" class="block py-2 text-slate-300 hover:text-sky-400 text-sm font-medium">
                Why Us
            </a>
            <div class="pt-3 border-t border-slate-800/80 flex flex-col gap-2">
                <template v-if="authStore.isAuthenticated">
                    <router-link to="/studio" @click="mobileMenuOpen = false"
                                class="w-full text-center py-2.5 rounded-xl text-sm font-semibold bg-sky-500 text-slate-950">
                        Creator Studio
                    </router-link>
                    <button @click="handleLogout"
                            class="w-full text-center py-2 text-sm text-rose-400 hover:bg-rose-500/10 rounded-xl">
                        Sign Out
                    </button>
                </template>
                <template v-else>
                    <router-link to="/login" @click="mobileMenuOpen = false"
                                class="w-full text-center py-2 rounded-xl text-sm text-slate-300 hover:bg-slate-800">
                        Sign In
                    </router-link>
                    <router-link to="/register" @click="mobileMenuOpen = false"
                                class="w-full text-center py-2.5 rounded-xl text-sm font-semibold bg-sky-500 text-slate-950">
                        Get Started Free
                    </router-link>
                </template>
            </div>
        </div>
    </nav>
</template>
<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../store/auth';
import { ShieldCheck, LayoutDashboard, LogOut, ArrowRight, Menu, X } from 'lucide-vue-next';

const authStore = useAuthStore();
const router = useRouter();
const mobileMenuOpen = ref(false);

const handleLogout = () => {
    authStore.logout();
    mobileMenuOpen.value = false;
    router.push('/');
}
</script>