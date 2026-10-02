<template>
    <div class="min-h-screen flex flex-col items-center justify-between p-4 sm:p-6 transition-colors duration-300 relative" :style="backgroundStyle">
        <!-- Top Verified Badge -->
        <div class="w-full max-w-md flex items-center justify-end mb-4">
            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-[11px] font-medium bg-slate-900/60 backdrop-blur-md border border-white/10 text-slate-300">
                <ShieldCheck class="w-3.5 h-3.5 text-sky-400" />
                Verified Creator Storefront
            </span>
        </div>
        <!-- Main Creator Bio Column -->
        <div class="w-full max-w-md flex flex-col items-center text-center">
            <!-- Avatar -->
            <div class="relative mb-3">
                <div class="w-24 h-24 rounded-full p-0.5 shadow-xl"
                        :style="{ background: `linear-gradient(135deg, ${activeTheme.primary || '#38bdf8'}, #6366f1)`}">
                    <div class="w-full h-full rounded-full overflow-hidden bg-slate-900 flex items-center justify-center">
                        <img v-if="creatorProfile?.avatarUrl" :src="creatorProfile.avatarUrl"
                                :alt="creatorProfile.displayName" class="w-full h-full object-cover" />
                        <span v-else class="text-2xl font-bold text-white">
                            {{ (creatorProfile?.displayName || 'C').charAt(0).toUpperCase() }}
                        </span>
                    </div>
                </div>
            </div>
            <!-- Creator Name & Bio -->
            <h1 class="text-xl font-bold text-white tracking-tight">
                {{ creatorProfile?.displayName || 'Creator' }}
            </h1>
            <p class="text-xs text-slate-400 mt-0.5 font-mono">@{{ creatorProfile?.username }}</p>
            <p v-if="creatorProfile?.bio" class="text-xs text-slate-300 mt-2.5 max-w-xs leading-relaxed">
                {{ creatorProfile.bio }}
            </p>
            <!-- Links & Custom Blocks -->
            <div class="w-full mt-6 space-y-3">
                <div v-for="block in blocks" :key="block.id" class="w-full">
                    <!-- Header Type Block -->
                    <div v-if="block.blockType === 'HEADER'"
                        class="pt-3 pb-1 text-xs font-semibold uppercase tracking-wider text-slate-400 text-left px-1">
                        {{ block.title }}
                    </div>
                    <!-- 2. Social Block (Brand Icon & Badge) -->
                    <a v-else-if="block.blockType === 'SOCIALS'" :href="block.url" target="_blank" rel="noopener noreferrer"
                        class="w-full py-2.5 px-4 flex items-center justify-between text-xs font-medium transition-all duration-200"
                        :class="[buttonShapeClass, buttonStyleClass]"
                        :style="buttonCustomStyle">
                        <div class="flex items-center gap-2.5 truncate">
                            <div :class="['w-6 h-6 rounded-lg flex items-center justify-center border shrink-0', getSocialInfo(block.url, block.title).bg]">
                                <component :is="getSocialInfo(block.url, block.title).icon"
                                            :class="['w-3.5 h-3.5', getSocialInfo(block.url, block.title).color]" />
                            </div>
                            <span class="truncate font-semibold">{{ block.title }}</span>
                        </div>
                        <ExternalLink class="w-3.5 h-3.5 opacity-50 shrink-0" />
                    </a>
                    <!-- Standard Link Block -->
                    <a v-else :href="block.url" target="_blank" rel="noopener noreferrer"
                        class="w-full py-3.5 px-5 flex items-center justify-between text-sm font-semibold transition-all duration-200"
                        :class="[buttonShapeClass, buttonStyleClass]" :style="buttonCustomStyle">
                        <div class="flex items-center gap-3 truncate">
                            <Globe class="w-4 h-4 shrink-0 opacity-70" />
                            <span class="truncate">{{ block.title }}</span>
                        </div>
                        <ExternalLink class="w-4 h-4 opacity-50 shrink-0" />
                    </a>
                </div>
            </div>
            <!-- Digital Products Section -->
            <div v-if="products && products.length > 0" class="w-full mt-8 text-left">
                <div class="flex items-center justify-between mb-3 px-1">
                    <span class="text-xs font-semibold uppercase tracking-wider text-slate-400">
                        Digital Products
                    </span>
                    <span class="text-[10px] text-cyan-400 flex items-center gap-1 font-medium">
                        <ShieldCheck class="w-3.5 h-3.5" />
                        Anti-Piracy Protected
                    </span>
                </div>
                <div class="space-y-3">
                    <div v-for="product in products" :key="product.id"
                            class="p-4 rounded-2xl bg-slate-900/80 border border-slate-800 shadow-lg flex items-center gap-4 transition-all duration-200 hover:border-slate-700">
                        <div class="w-14 h-16 rounded-xl bg-slate-800 overflow-hidden shrink-0 border border-slate-700 flex items-center justify-center">
                            <img v-if="product.coverImageUrl" :src="product.coverImageUrl"
                                    :alt="product.title" class="w-full h-full object-cover" />
                            <FileText v-else class="w-6 h-6 text-sky-400" />
                        </div>    
                        <div class="flex-1 min-w-0">
                            <h3 class="text-sm font-bold text-white truncate">
                                {{ product.title }}
                            </h3>
                            <p class="text-xs text-slate-400 truncate mt-0.5">
                                {{ product.description || 'Protected PDF E-Book' }}
                            </p>
                            <div class="flex items-center justify-between mt-2">
                                <span class="text-sm font-extrabold text-sky-400">
                                    {{ formatCurrency(product.price) }}
                                </span>
                                <button @click="openCheckout(product)"
                                        class="px-3.5 py-1.5 rounded-xl text-xs font-bold bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 shadow-md shadow-sky-500/20 hover:opacity-90 transition-all cursor-pointer">
                                    Buy Now
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Footer -->
        <div class="mt-12 py-4 flex items-center gap-1.5 text-xs text-slate-500">
            <ShieldCheck class="w-3.5 h-3.5 text-sky-400" />
            <span>Powered by
                <router-link to="/" class="text-slate-400 hover:text-white font-semibold">
                    LumenBio
                </router-link>
            </span>
        </div>
        <!-- Checkout Modal Component -->
        <CheckoutModal :isOpen="isCheckoutOpen" :product="selectedProduct" @close="isCheckoutOpen = false" />        
    </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { usePublicStore } from '../store/publicStore.js';
import CheckoutModal from '../components/checkout/CheckoutModal.vue';
import { ShieldCheck, Globe, ExternalLink, FileText, Instagram, Youtube, Facebook, Twitter, Github, Linkedin, Share2 } from 'lucide-vue-next';

const route = useRoute();
const publicStore = usePublicStore();

const isCheckoutOpen = ref(false);
const selectedProduct = ref(null);

const creatorProfile = computed(() => publicStore.creatorProfile);
const blocks = computed(() => publicStore.blocks);
const products = computed(() => publicStore.products);

const activeTheme = computed(() => {
    if (creatorProfile.value?.themeConfigJson) {
        try {
            return JSON.parse(creatorProfile.value.themeConfigJson);
        } catch (e) {
            return {};
        }
    }
    return { bg: '#0b0f19', primary: '#38bdf8', cardStyle: 'glass', buttonShape: 'rounded-xl'};
});

const backgroundStyle = computed(() => {
    const bg = activeTheme.value.bg || '#0b0f19';
    if (bg.startsWith('linear-gradient') || bg.startsWith('radial-gradient')) {
        return { background: bg };
    }
    return { backgroundColor: bg };
});

const buttonShapeClass = computed(() => {
    const shape = activeTheme.value.buttonShape || 'rounded-xl';
    switch (shape) {
        case 'rounded-full' : return 'rounded-full';
        case 'rounded-none' : return 'rounded-none';
        case 'rounded-md' : return 'rounded-md';
        default: return 'rounded-xl';
    }
});

const buttonStyleClass= computed(() => {
    const style = activeTheme.value.cardStyle || 'glass';
    switch (style) {
        case 'solid': return 'text-slate-950 shadow-md font-semibold hover:opacity-90';
        case 'outline': return 'bg-transparent border border-slate-600 text-slate-200 hover:border-slate-400';
        case 'neon': return 'bg-slate-950 border text-cyan-300 shadow-[0_0_12px_rgba(56,189,248,0.25)]';
        default: return 'bg-slate-800/60 backdrop-blur-md border border-white/10 text-slate-100 hover:bg-slate-800/80 shadow-sm';
    }
});

const buttonCustomStyle = computed(() => {
    const style = activeTheme.value.cardStyle || 'glass';
    const primary = activeTheme.value.primary || '#38bdf8';
    if (style === 'solid') return { backgroundColor: primary };
    if (style === 'outline' || style === 'neon') return { borderColor: primary };
    return {};
});

const formatCurrency = (val) => {
    if (!val) return 'Rp 0';
    return new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0}).format(val);
};

const openCheckout = (product) => {
    selectedProduct.value = product;
    isCheckoutOpen.value = true;
};

const getSocialInfo = (url = '', title = '') => {
    const target = (url + ' ' + title).toLowerCase();

    if (target.includes('instagram.com') || target.includes('instagram')) {
        return { icon: Instagram, color: 'text-pink-400', bg: 'bg-pink-500/10 border-pink-500/20' };
    }
    if (target.includes('youtube.com') || target.includes('youtube')) {
        return { icon: Youtube, color: 'text-rose-400', bg: 'bg-rose-500/10 border-rose-500/20' };
    }
    if (target.includes('facebook.com') || target.includes('facebook')) {
        return { icon: Facebook, color: 'text-blue-400', bg: 'bg-blue-500/10 border-blue-500/20' };
    }
    if (target.includes('github.com') || target.includes('github')) {
        return { icon: Github, color: 'text-slate-100', bg: 'bg-slate-700/30 border-slate-600/30' };
    }
    if (target.includes('twitter.com') || target.includes('x.com') || target.includes('twitter')) {
        return { icon: Twitter, color: 'text-sky-400', bg: 'bg-sky-500/10 border-sky-500/20' };
    }
    if (target.includes('linkedin.com') || target.includes('linkedin')) {
        return { icon: Linkedin, color: 'text-cyan-400', bg: 'bg-cyan-500/10 border-cyan-500/20' };
    }
    return { icon: Share2, color: 'text-purple-400', bg: 'bg-purple-500/10 border-purple-500/20' };
};

onMounted(async () => {
    const username = route.params.username;
    if (username) {
        await publicStore.fetchPublicProfile(username);
    }
});
</script>