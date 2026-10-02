<template>
    <div class="h-screen w-screen flex flex-col bg-slate-950 select-none overflow-hidden"
        @contextmenu.prevent="showWarning('Rightclick is disabled to protect copyrighted content.')">
        <!-- Top Security Header Bar -->
        <header class="h-14 px-4 sm:px-6 bg-slate-900/90 border-b border-slate-800 flex items-center justify-between shrink-0 z-30">
            <div class="flex items-center gap-3">
                <router-link to="/" class="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors">
                    <ArrowLeft class="w-4 h-4" />
                </router-link>
                <div class="flex items-center gap-2">
                    <ShieldCheck class="w-4 h-4 text-emerald-400" />
                    <span class="text-xs font-semibold text-slate-200">
                        Protected Document Reader
                    </span>
                </div>
            </div>
            <!-- License Stamp Indicator -->
            <div class="hidden sm:flex items-center gap-2 px-3 py-1 rounded-full bg-slate-800/80 border border-slate-700/60 text-xs text-slate-400">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
                <span>Licensed DRM Session • Order #{{ orderId?.substring(0, 8) }}</span>
            </div>
            <!-- Exit Button -->
            <button @click="$router.push('/')"
                    class="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors">
                <X class="w-4 h-4" />
            </button>
        </header>
        <!-- Main Canvas Viewer Area -->
        <main :class="['flex-1 relative overflow-hidden transition-all duration-200', isUnfocused ? 'unfocused-blur' : '']">
            <SecurePdfCanvas v-if="pdfUrl" :pdfUrl="pdfUrl" :buyerEmail="buyerEmail" :orderId="orderId" />
        </main>
        <!-- Tab Unfocused Security Shield Overlay -->
        <div v-if="isUnfocused" class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-xl flex flex-col items-center justify-center p-6 text-center">
            <div class="w-14 h-14 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-amber-400 flex items-center justify-center mb-4 shadow-lg shadow-amber-500/10">
                <ShieldAlert class="w-7 h-7" />
            </div>
            <h2 class="text-lg font-bold text-white mb-1">Content Hidden While Window is Inactive</h2>
            <p class="text-xs text-slate-400 max-w-sm">
                To prevent screen recording and unauthorized capture, this protected document is hidden until you return focus to this tab.
            </p>
        </div>
        <!-- Temporary Alert Banner -->
        <transition name="fade">
            <div v-if="alertMessage" class="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-xl bg-rose-500/90 text-white text-xs font-semibold shadow-xl flex items-center gap-2">
                <AlertCircle class="w-4 h-4" />
                <span>{{ alertMessage }}</span>
            </div>
        </transition>
    </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import SecurePdfCanvas from '../components/drm/SecurePdfCanvas.vue';
import { ShieldCheck, ArrowLeft, X, ShieldAlert, AlertCircle } from 'lucide-vue-next';

const route = useRoute();
const orderId = ref(route.params.orderId);
const pdfUrl = ref(`/api/content/read/${orderId.value}`);
const buyerEmail = ref('buyer@domain.com');

const isUnfocused = ref(false);
const alertMessage = ref('');
let alertTimer = null;

const showWarning = (msg) => {
    alertMessage.value = msg;
    clearTimeout(alertTimer);
    alertTimer = setTimeout(() => {
        alertMessage.value = '';
    }, 3000);
};

// Security Interceptor for Shortcuts
const handleKeydown = (e) => {
    // Intercept Ctrl+P, Ctrl+S, Ctrl+C, Ctrl+U, PrintScreen, F12
    if ((e.ctrlKey || e.metaKey) && ['p', 's', 'c', 'u'].includes(e.key.toLowerCase())) {
        e.preventDefault();
        showWarning(`Action (Ctrl+${e.key.toUpperCase()}) blocked by Anti-Piracy Shield.`);
    }
    if (e.key === 'PrintScreen') {
        showWarning('Screen capture attempt intercepted.');
        navigator.clipboard.writeText('Protected by LumenBio Anti-Piracy DRM.');
    }
};

const handleVisibilityChange = () => {
    isUnfocused.value = document.hidden;
};

const handleWindowBlur = () => {
    isUnfocused.value = true;
};

const handleWindowFocus = () => {
    isUnfocused.value = false;
}

onMounted(() => {
    window.addEventListener('keydown', handleKeydown);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    window.addEventListener('blur', handleWindowBlur);
    window.addEventListener('focus', handleWindowFocus);
});

onUnmounted(() => {
    window.removeEventListener('keydown', handleKeydown);
    document.removeEventListener('visibilitychange', handleVisibilityChange);
    window.removeEventListener('blur', handleWindowBlur);
    window.removeEventListener('focus', handleWindowFocus);
});
</script>