<template>
    <div class="min-h-screen flex flex-col justify-center items-center px-4 py-12 bg-[#0b0f19] glow-mesh relative">
        <!-- Return to Home Link -->
        <router-link to="/" class="absolute top-8 left-8 flex items-center gap-2 text-xs font-semibold text-slate-400 hover:text-white transition-colors">
            <ArrowLeft class="w-4 h-4" />
            Back to LumenBio
        </router-link>
        <div class="w-full max-w-md">
            <!-- Brand Badge -->
            <div class="flex flex-col items-center mb-8 text-center">
                <div class="w-12 h-12 rounded-2xl bg-gradient-to-r from-sky-500 to-cyan-400 p-0.5 shadow-xl shadow-sky-500/20 mb-3">
                    <div class="w-full h-full bg-[#0b0f19] rounded-[14px] flex items-center justify-center">
                        <ShieldCheck class="w-6 h-6 text-sky-400" />
                    </div>
                </div>
                <h1 class="text-2xl font-bold text-white font-display">
                    {{ isRegister ? 'Create Your Creator Account' : 'Sign in to Creator Studio' }}
                </h1>
                <p class="text-xs text-slate-400 mt-1">
                    {{ isRegister ? 'Start monetizing your bio-link with anti-piracy protection' : 'Manage your storefront, DRM products, and revenue' }}
                </p>
            </div>
            <!-- Tab Switcher -->
            <div class="grid grid-cols-2 gap-1 p-1 bg-slate-900/80 rounded-2xl border border-slate-800 mb-6">
                <button @click="setMode(false)"
                        :class="['py-2 text-xs font-semibold rounded-xl transition-all cursor-pointer', !isRegister ? 'bg-sky-500 text-slate-950 shadow-md' : 'text-slate-400 hover:text-white']">
                    Sign In
                </button>
                <button @click="setMode(true)"
                        :class="['py-2 text-xs font-semibold rounded-xl transition-all cursor-pointer', isRegister ? 'bg-sky-500 text-slate-950 shadow-md' : 'text-slate-400 hover:text-white']">
                    Create Account
                </button>
            </div>
            <!-- Error Banner -->
            <div v-if="error" class="mb-5 p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-xs flex items-center gap-2">
                <AlertCircle class="w-4 h-4 shrink-0 text-rose-400" />
                <span>{{ error }}</span>
            </div>
            <!-- Form Card -->
            <div class="glass-card p-6 sm:p-8 rounded-3xl border border-slate-800/80 shadow-2xl">
                <form @submit.prevent="handleSubmit" class="space-y-4">
                    <!-- Register-only fields -->
                    <template v-if="isRegister">
                        <div>
                            <label class="block text-xs font-semibold text-slate-300 mb-1">Display Name</label>
                            <input v-model="form.displayName" type="text" required placeholder="e.g. Sarah Jenkins"
                                    class="w-full px-4 py-2.5 rounded-xl glass-input text-sm placeholder:text-slate-500" />
                        </div>
                        <div>
                            <label class="block text-xs font-semibold text-slate-300 mb-1">
                                Custom Username
                            </label>
                            <div class="relative">
                                <span class="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 font-mono text-sm">@</span>
                                <input v-model="form.username" type="text" required placeholder="username"
                                        class="w-full pl-8 pr-4 py-2.5 rounded-xl glass-input text-sm placeholder:text-slate-500 font-mono" />
                            </div>
                            <p class="text-[10px] text-slate-400 mt-1">
                                Your public bio will be: <span class="text-sky-400 font-mono">
                                    lumenbio.me/@{{ form.username || 'username' }}
                                </span>
                            </p>
                        </div>
                    </template>
                    <!-- Email -->
                    <div>
                        <label class="block text-xs font-semibold text-slate-300 mb-1">Email Address</label>
                        <input v-model="form.email" type="email" required placeholder="creator@domain.com"
                                class="w-full px-4 py-2.5 rounded-xl glass-input text-sm placeholder:text-slate-500" />
                    </div>
                    <!-- Password -->
                    <div>
                        <label class="block text-xs font-semibold text-slate-300 mb-1">
                            Password
                        </label>
                        <input v-model="form.password" type="password" required placeholder="********" minlength="6"
                                class="w-full px-4 py-2.5 rounded-xl glass-input text-sm placeholder:text-slate-500" />
                    </div>
                    <button type="submit" :disabled="loading" 
                            class="w-full mt-3 py-3 rounded-xl font-bold text-sm bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 hover:opacity-95 shadow-lg shadow-sky-500/20 transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50">
                        <Loader2 v-if="loading" class="w-4 h-4 animate-spin" />
                        <span v-else>{{ isRegister ? 'Create Account & Launch Studio' : 'Sign In to Studio' }}</span>
                    </button>
                </form>
            </div>
        </div>
    </div>
</template> 
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { ShieldCheck, ArrowLeft, AlertCircle, Loader2 } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isRegister = ref(route.path === '/register');
const loading = ref(false);
const error = ref('');

const form = reactive({
    displayName: '',
    username: '',
    email: '',
    password: ''
});

const setMode = (register) => {
    isRegister.value = register;
    error.value = '';

    window.history.replaceState(null, '', register ? '/register' : '/login');
};

onMounted(() => {
    if (route.query.username) {
        form.username = String(route.query.username).toLowerCase();
        isRegister.value = true;
    }
});

const handleSubmit = async () => {
    loading.value = true;
    error.value = '';
    try {
        if(isRegister.value) {
            await authStore.register({
                displayName: form.displayName,
                username: form.username.toLowerCase(),
                email: form.email,
                password: form.password
            });
        } else {
            await authStore.login(form.email, form.password);
        }
        router.push('/studio');
    } catch (err) {
        error.value = authStore.error || 'Authentication failed. Please check your credentials.';
    } finally {
        loading.value = false;
    }
};
</script>