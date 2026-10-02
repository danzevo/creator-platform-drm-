<template>
    <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 overflow-y-auto">
        <!-- Backdrop Blur Overlay -->
        <div class="fixed inset-0 bg-black/80 backdrop-blur-md transition-opacity duration-300"
            @click="handleClose"></div>
        <!-- Modal Card Container -->
        <div class="relative w-full max-w-lg glass-card rounded-3xl border border-slate-700/80 shadow-2xl overflow-hidden z-10 my-auto animate-in fade-in zoom-in-95 duration-200">
            <!-- Modal Header -->
            <div class="flex items-center justify-between px-6 py-4 border-b border-slate-800/80 bg-slate-900/60">
                <div class="flex items-center gap-2">
                    <div class="w-7 h-7 rounded-lg bg-sky-500/10 border border-sky-500/20 flex items-center justify-center">
                        <ShieldCheck class="w-4 h-4 text-sky-400" />
                    </div>
                    <span class="text-xs font-semibold text-sky-300 uppercase tracking-wider">
                        Secure DRM Checkout
                    </span>
                </div>
                <button @click="handleClose" class="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors">
                    <X class="w-5 h-5" />
                </button>
            </div>
            <!-- Modal Content Body -->
            <div class="p-6">
                <!-- STEP 1: Buyer Information Form -->
                <div v-if="step === 'form'" class="space-y-5">
                    <!-- Product Summary Card -->
                    <div class="flex items-center gap-4 p-3.5 rounded-2xl bg-slate-900/70 border border-slate-800">
                        <div class="w-14 h-16 rounded-xl bg-slate-800 overflow-hidden flex items-center justify-center shrink-0 border border-slate-700/60">
                            <img v-if="product?.coverImageUrl" :src="product.coverImageUrl" :alt="product?.title" class="w-full h-full object-cover" />
                            <FileText v-else class="w-6 h-6 text-sky-400" />
                        </div>
                        <div class="flex-1 min-w-0">
                            <h3 class="text-sm font-bold text-white truncate">{{ product?.title }}</h3>
                            <p class="text-xs text-slate-400 truncate mt-0.5">{{ product?.description || 'Protected Digital E-Book / PDF' }}</p>
                            <div class="text-sm font-extrabold text-sky-400 mt-1">
                                {{ formatCurrency(product?.price) }}
                            </div>
                        </div>
                    </div>
                    <!-- Anti-Piracy Watermark Notice -->
                    <div class="p-3 rounded-xl bg-sky-500/5 border border-sky-500/20 flex items-start gap-2.5">
                        <Lock class="w-4 h-4 text-sky-400 shrink-0 mt-0.5" />
                        <p class="text-[11px] text-slate-300 leading-relaxed">
                            <strong class="text-sky-300">Anti-Piracy Notice:</strong>  Upon purchase, this PDF will be stamped with an indelible watermark containing your buyer email & phone number.
                        </p>
                    </div>
                    <!-- Input Fields -->
                    <form @submit.prevent="handleCreateOrder" class="space-y-4">
                        <div>
                            <label class="block text-xs font-semibold text-slate-300 mb-1.5">
                                Buyer Email Address <span class="text-rose-400">*</span>
                            </label>
                            <div class="relative">
                                <Mail class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                                <input v-model="form.email" type="email" required placeholder="name@domain.com"
                                    class="w-full pl-10 pr-4 py-2.5 rounded-xl glass-input text-sm placeholder:text-slate-500 focus:ring-1 focus:ring-sky-500" />
                            </div>
                            <p class="text-[10px] text-slate-400 mt-1">
                                Order receipt and access token will be tied to this email.
                            </p>
                        </div>
                        <div>
                            <label class="block text-xs font-semibold text-slate-300 mb-1.5">
                                WhatsApp / Phone Number <span class="text-rose-400">*</span>
                            </label>
                            <div class="relative">
                                <Smartphone class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                                <input v-model="form.phone" type="tel" required placeholder="081234567890"
                                    class="w-full pl-10 pr-4 py-2.5 rounded-xl glass-input text-sm placeholder:text-slate-500 focus:ring-1 focus:ring-sky-500" />
                            </div>
                        </div>
                        <!-- Payment Method Selector -->
                        <div>
                            <label class="block text-xs font-semibold text-slate-300 mb-2">Select Payment Method</label>
                            <div class="grid grid-cols-2 gap-3">
                                <label :class="['flex items-center gap-2.5 p-3 rounded-xl border cursor-pointer transition-all duration-200', form.paymentMethod === 'QRIS' ? 'bg-sky-500/10 border-sky-500 text-white' : 'bg-slate-900/60 border-slate-800 text-slate-400 hover:border-slate-700']">
                                    <input type="radio" v-model="form.paymentMethod" value="QRIS" class="sr-only" />
                                    <QrCode class="w-4 h-4 text-sky-400" />
                                    <span class="text-xs font-semibold">QRIS (Instant)</span>    
                                </label>
                                <label :class="['flex items-center gap-2.5 p-3 rounded-xl border cursor-pointer transition-all duration-200', form.paymentMethod === 'BCA_VA' ? 'bg-sky-500/10 border-sky-500 text-white' : 'bg-slate-900/60 border-slate-800 text-slate-400 hover:border-slate-700']">
                                    <input type="radio" v-model="form.paymentMethod" value="BCA_VA" class="sr-only" />
                                    <CreditCard class="w-4 h-4 text-sky-400" />
                                    <span class="text-xs font-semibold">BCA Virtual Account</span>
                                </label>
                            </div>
                        </div>
                        <!-- Submit Button -->
                        <button type="submit" :disabled="submitting"
                                class="w-full mt-2 py-3 rounded-xl font-semibold text-sm bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 shadow-lg shadow-sky-500/20 hover:opacity-95 transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer">
                            <Loader2 v-if="submitting" class="w-4 h-4 animate-spin" />
                            <span v-else>Proceed to Payment • {{ formatCurrency(product?.price) }}</span>
                        </button>
                    </form>
                </div>
                <!-- STEP 2: Payment Display & Polling Screen -->
                <div v-else-if="step === 'payment'" class="space-y-5 text-center">
                    <div class="flex items-center justify-center gap-2 text-xs font-semibold text-slate-400">
                        <span>Time remaining:</span>
                        <span class="text-amber-400 font-mono text-sm font-bold">{{ countdownFormatted }}</span>
                    </div>
                    <!-- QRIS Presentation Box -->
                    <div v-if="orderData?.paymentMethod === 'QRIS'" class="p-6 rounded-2xl bg-white text-slate-950 inline-block shadow-xl">
                        <div class="flex flex-col items-center">
                            <span class="text-xs font-extrabold tracking-widest text-slate-800 mb-2 uppercase">
                                QRIS STANDAR PEMBAYARAN NASIONAL
                            </span>
                            <!-- Clean QRIS Representation -->
                            <div class="w-48 h-48 bg-slate-50 border-2 border-slate-900 rounded-xl p-2 flex items-center justify-center relative">
                                <QrCode class="w-40 h-40 text-slate-950" />
                                <div class="absolute inset-0 flex items-center justify-center">
                                    <div class="w-8 h-8 rounded-md bg-white border border-slate-800 flex items-center justify-center shadow-sm">
                                        <ShieldCheck class="w-5 h-5 text-sky-600" />
                                    </div>
                                </div>
                            </div>
                            <span class="text-[10px] text-slate-600 mt-2 font-mono font-medium">
                                BCA, Mandiri, GoPay, OVO, Dana, ShopeePay
                            </span>
                        </div>
                    </div>
                    <!-- Virtual Account Box -->
                    <div v-else class="p-5 rounded-2xl bg-slate-900 border border-slate-800 text-left space-y-2">
                        <span class="text-xs text-slate-400">Nomor Virtual Account:</span>
                        <div class="flex items-center justify-between bg-slate-950 px-4 py-2.5 rounded-xl border border-slate-800">
                            <span class="font-mono text-base font-bold text-sky-400">{{ orderData?.virtualAccount || '8808' + form.phone }}</span>
                            <button @click="copyVA" class="text-xs text-slate-300 hover:text-white flex items-center gap-1">
                                <Copy class="w-3.5 h-3.5" />
                                {{ copied ? 'Copied' : 'Copy' }}
                            </button>
                        </div>
                    </div>
                    <!-- Payment Details Table -->
                    <div class="p-3.5 rounded-xl bg-slate-900/60 border border-slate-800 text-xs space-y-1.5">
                        <div class="flex justify-between text-slate-400">
                            <span>Order ID:</span>
                            <span class="font-mono text-slate-200">#{{ orderData?.orderId?.substring(0, 8) }}</span>
                        </div>
                        <div class="flex justify-between text-slate-400">
                            <span>Total Amount:</span>
                            <span class="font-bold text-white">{{ formatCurrency(orderData?.totalAmount) }}</span>
                        </div>
                    </div>
                    <!-- Live Checking Status Indicator -->
                    <div class="flex items-center justify-center gap-2 text-xs text-sky-400">
                        <Loader2 class="w-3.5 h-3.5 animate-spin" />
                        <span>Waiting for payment... Checking automatically</span>
                    </div>
                    <!-- Developer Instant Simulation Button -->
                    <div class="pt-2 border-t border-slate-800/80">
                        <button @click="handleSimulatePayment" :disabled="simulating"
                                class="w-full py-2.5 rounded-xl text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 hover:bg-emerald-500/20 transition-all flex items-center justify-center gap-1.5 cursor-pointer">
                            <Sparkles class="w-3.5 h-3.5" />
                            <span>{{ simulating ? 'Processing simulation...' : '⚡ Simulate Instant Payment (Dev Sandbox)' }}</span>    
                        </button>
                    </div>
                </div>
                <!-- STEP 3: Payment Success & Read Access Screen -->
                <div v-else-if="step === 'success'" class="space-y-6 text-center py-4">
                    <div class="w-16 h-16 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 flex items-center justify-center mx-auto shadow-lg shadow-emerald-500/10">
                        <CheckCircle2 class="w-9 h-9" />
                    </div>
                    <div>
                        <h3 class="text-lg font-bold text-white">Payment Confirmed!</h3>
                        <p class="text-xs text-slate-400 mt-1 max-w-xs mx-auto">
                            Your payment has been settled. The anti-piracy worker is stamping your dynamic buyer watermark.
                        </p>
                    </div>
                    <div class="p-3.5 rounded-2xl bg-slate-900 border border-slate-800 text-xs text-left space-y-1">
                        <div class="flex justify-between">
                            <span class="text-slate-400">Document:</span>
                            <span class="font-semibold text-white truncate max-w-[200px]">{{ product?.title }}</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-slate-400">Licensed to:</span>
                            <span class="font-semibold text-sky-400">{{ form.email }}</span>
                        </div>
                    </div>
                    <!-- Direct Access CTA -->
                    <button @click="navigateToReader" class="w-full py-3.5 rounded-xl font-bold text-sm bg-gradient-to-r from-emerald-400 to-teal-500 text-slate-950 shadow-lg shadow-emerald-500/20 hover:opacity-95 transition-all flex items-center justify-center gap-2 cursor-pointer">
                        <BookOpen class="w-4 h-4" />
                        <span>Open Protected Document Now</span>
                        <ArrowRight class="w-4 h-4" />
                    </button>
                </div>
            </div>
        </div>
    </div>    
</template>
<script setup>
import { ref, reactive, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { usePublicStore } from '../../store/publicStore';
import confetti from 'canvas-confetti';
import { ShieldCheck, X, FileText, Lock, Mail, Smartphone,
         QrCode, CreditCard, Loader2, Copy, Sparkles, CheckCircle2, BookOpen, ArrowRight
} from 'lucide-vue-next';

const props = defineProps({
    isOpen: { type: Boolean, default: false },
    product: { type: Object, default: null }
});

const emit = defineEmits(['close']);
const router = useRouter();
const publicStore = usePublicStore();

const step = ref('form') // 'form' | 'payment' | 'success'
const submitting = ref(false);
const simulating = ref(false);
const copied = ref(false);
const orderData = ref(null);
const countDown = ref(900); // 15 minutes in seconds
let countDownTimer = null;
let pollTimer = null;

const form = reactive({
    email: '',
    phone: '',
    paymentMethod: 'QRIS'
});

const countdownFormatted = computed(() => {
    const mins = Math.floor(countDown.value / 60);
    const secs = countDown.value % 60;
    
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
});

const formatCurrency = (val) => {
    if (!val) return 'Rp 0';
    return Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0}).format(val);
};

const handleCreateOrder = async () => {
    if (!props.product?.id) return;
    submitting.value = true;
    try {
        const res = await publicStore.createOrder({
            productId: props.product.id,
            buyerEmail: form.email,
            buyerPhone: form.phone,
            paymentMethod: form.paymentMethod
        });
        orderData.value = res;
        step.value = 'payment';
        startCountdown();
        startPolling(res.orderId);
    } catch (err) {
        alert(err.response?.data?.message || 'Failed to initialize order checkout');
    } finally {
        submitting.value = false;
    }
};

const startCountdown = () => {
    clearInterval(countDownTimer);
    countDown.value = 900;
    countDownTimer = setInterval(() => {
        if (countDown.value > 0) {
            countDown.value--;
        } else {
            clearInterval(countDownTimer);
        }
    }, 1000);
}

const startPolling = (orderId) => {
    clearInterval(pollTimer);
    pollTimer = setInterval(async () => {
        try {
            const statusRes = await publicStore.getOrderStatus(orderId);
            if (statusRes.status === 'PAID') {
                clearInterval(pollTimer);
                clearInterval(countDownTimer);
                step.value = 'success';
                triggerCelebration();
            }
        } catch (e) {
            console.error('Polling check failed:', e)
        }
    }, 2500);
}

const handleSimulatePayment = async () => {
    if (!orderData.value?.orderId) return;
    simulating.value = true;
    try {
        await publicStore.simulatePayment(orderData.value.orderId);
        // Force immediate status check
        const statusRes = await publicStore.getOrderStatus(orderData.value.orderId);
        if (statusRes.status === 'PAID') {
            clearInterval(pollTimer);
            clearInterval(countDownTimer);
            step.value = 'success';
            triggerCelebration();
        }
    } catch (err) {
        alert('Payment simulation failed');
    } finally {
        simulating.value = false
    }
};

const triggerCelebration = () => {
    confetti({
        particleCount: 80,
        spread: 70,
        origin: { y: 0.6 }
    });
};

const copyVA = () => {
    const val = orderData.value?.virtualAccount || '8808' + form.phone;
    navigator.clipboard.writeText(val);
    copied.value = true;
    setTimeout(() => { copied.value = false; }, 2000);
};

const navigateToReader = () => {
    const id = orderData.value?.orderId;
    handleClose();
    if (id) {
        router.push(`/read/${id}`);
    }
};

const handleClose = () => {
    clearInterval(countDownTimer);
    clearInterval(pollTimer);
    step.value = 'form';
    orderData.value = null;
    emit('close');
}

onUnmounted(() => {
    clearInterval(countDownTimer);
    clearInterval(pollTimer);
})
</script>