<template>
    <div class="relative mx-auto flex flex-col items-center select-none">
        <!-- iPhone Chassis -->
        <div class="relative w-[340px] sm:w-[370px] h-[720px] bg-slate-950 rounded-[52px] p-3 shadow-[0_25px_60px_-15px_rgba(0,0,0,0.9)] border-[10px] border-slate-800 ring-1 ring-white/10 flex flex-col">
            <!-- Dynamic Island Notch & Speaker -->
            <div class="absolute top-4 left-1/2 -translate-x-1/2 z-30 flex items-center justify-center">
                <div class="w-28 h-7 bg-black rounded-full flex items-center justify-between px-2.5 shadow-md">
                    <div class="w-3 h-3 rounded-full bg-slate-900 border border-slate-800 flex items-center justify-center">
                        <div class="w-1.5 h-1.5 rounded-full bg-cyan-900/60"></div>
                    </div>
                    <div class="w-2.5 h-2.5 rounded-full bg-slate-900 border border-slate-800"></div>
                </div>
            </div>
            <!-- Screen Viewport Container -->
            <div class="w-full h-full rounded-[42px] overflow-y-auto overflow-x-hidden relative scrollbar-none flex flex-col transition-colors duration-300" :style="screenContainerStyle">
                <!-- Mobile Status Bar -->
                <div class="sticky top-0 z-20 flex items-center justify-between px-7 pt-3 pb-2 text-[11px] font-semibold text-slate-200/90 backdrop-blur-xs">
                    <span>9:41</span>
                    <div class="flex items-center gap-1.5">
                        <Signal class="w-3 h-3" />
                        <Wifi class="w-3 h-3" />
                        <Battery class="w-3.5 h-3.5" />
                    </div>
                </div>
                <!-- Bio Content Area -->
                <div class="px-5 pt-6 pb-12 flex-1 flex flex-col items-center text-center">
                    <!-- Creator Avatar -->
                    <div class="relative mb-3 group">
                        <div class="w-20 h-20 rounded-full p-0.5 shadow-xl transition-all duration-300" :style="{ background: `linear-gradient(135deg, ${activeTheme.primary || '#38bdf8'}, #6366f1)` }">
                            <div class="w-full h-full rounded-full overflow-hidden bg-slate-900 flex items-center justify-center">
                                <img v-if="profile?.avatarUrl" :src="profile.avatarUrl" :alt="profile.displayName || 'Avatar'"
                                        class="w-full h-full object-cover" />
                                <span v-else class="text-xl font-bold text-white">
                                    {{ (profile?.displayName || profile?.username || 'C').charAt(0).toUpperCase() }}
                                </span>
                            </div>
                        </div>
                        <span class="absolute bottom-0 right-0 w-5 h-5 bg-sky-500 rounded-full border-2 border-slate-950 flex items-center justify-center text-slate-950">
                            <Check class="w-3 h-3 stroke-[3]" />
                        </span>
                    </div>
                    <!-- Creator Info -->
                    <h2 class="text-base font-bold text-white tracking-tight">
                        {{ profile?.displayName || 'Your Name' }}
                    </h2>
                    <p class="text-xs text-slate-400 font-medium mt-0.5">
                        @{{ profile?.username || 'username' }}
                    </p>
                    <p class="text-xs text-slate-300/90 mt-2 max-w-[260px] leading-relaxed line-clamp-3">
                        {{ profile?.bio || 'Add a bio to tell your audience what you create and protect.' }}
                    </p>
                    <!-- Links & Blocks Section -->
                    <div class="w-full mt-6 space-y-3">
                        <div v-if="activeBlocks.length === 0"
                                class="py-6 border border-dashed border-slate-700/60 rounded-2xl text-xs text-slate-500">
                            No active links yet. Add your first link in the studio.
                        </div>
                        <div v-for="block in activeBlocks" :key="block.id" 
                            class="w-full transition-all duration-200 cursor-pointer"
                            @click="handleBlockClick(block)">
                            <!-- Header Block -->
                            <div v-if="block.blockType === 'HEADER'"
                                class="pt-2 pb-1 text-xs font-semibold uppercase tracking-wider text-slate-400 text-left px-1">
                                {{  block.title }}
                            </div>
                            <!-- 2. Social Block (Brand Icon & Badge) -->
                            <div v-else-if="block.blockType === 'SOCIALS'"
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
                            </div>
                            <!-- Link Block -->
                            <div v-else class="w-full py-3 px-4 flex items-center justify-between text-xs font-medium transition-all duration-200"
                                :class="[buttonShapeClass, buttonStyleClass]"
                                :style="buttonCustomStyle">
                                <div class="flex items-center gap-2.5 truncate">
                                    <Globe class="w-4 h-4 shrink-0 opacity-80" />
                                    <span class="truncate">{{ block.title }}</span>
                                </div>
                                <ExternalLink class="w-3.5 h-3.5 opacity-60 shrink-0" />
                            </div>
                        </div>
                    </div>
                    <!-- Digital Products Section -->
                    <div v-if="products && products.length > 0" class="w-full mt-6 text-left">
                        <div class="flex items-center justify-between mb-2.5 px-1">
                            <span class="text-xs font-semibold uppercase tracking-wider text-slate-400">
                                Digital Products
                            </span>
                            <span class="text-[10px] text-cyan-400 flex items-center gap-1 font-medium">
                                <ShieldCheck class="w-3 h-3" />
                                Anti-Piracy DRM
                            </span>
                        </div>
                        <div v-for="product in products" :key="product.id"
                            class="p-3 rounded-2xl bg-slate-900/80 border border-slate-800/90 shadow-md flex items-center gap-3 transition-all duration-200 hover:border-slate-700">
                            <div class="w-12 h-14 rounded-lg bg-gradient-to-br from-slate-800 to-slate-900 border border-slate-700/60 overflow-hidden shrink-0 flex items-center justify-center">
                                <img v-if="product.coverImageUrl"
                                    :src="product.coverImageUrl"
                                    :alt="product.title"
                                    class="w-full h-full object-cover" />
                                <FileText v-else class="w-5 h-5 text-sky-400" />
                            </div>
                            <div class="flex-1 min-w-0">
                                <h4 class="text-xs font-semibold text-white truncate">
                                    {{ product.title }}
                                </h4>
                                <p class="text-[10px] text-slate-400 truncate mt-0.5">
                                    {{ product.description || 'Protected E-Book / PDF' }}
                                </p>
                                <div class="flex items-center justify-between mt-1.5">
                                    <span class="text-xs font-bold text-sky-400">
                                        {{ formatCurrency(product.price) }}
                                    </span>
                                    <span class="px-2 py-0.5 rounded-full text-[9px] font-semibold bg-sky-500/10 text-sky-300 border border-sky-500/20">
                                        Buy
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Platform Watermark Footer -->
                <div class="mt-auto pt-8 flex items-center justify-center gap-1.5 text-[10px] text-slate-500">
                    <ShieldCheck class="w-3 h-3 text-sky-400" />
                    <span>Protected by CreatorLock</span>
                </div>
            </div>
            <!-- Home Indicator Bar -->
            <div class="absolute bottom-2 left-1/2 -translate-x-1/2 w-32 h-1 bg-slate-600/70 rounded-full pointer-events-none"></div>
        </div>
    </div>
</template>
<script setup>
import { computed } from 'vue';
import { Signal, Wifi, Battery, Check, Globe, ExternalLink, ShieldCheck, FileText, Instagram, Youtube, Facebook, Twitter, Github, Linkedin, Share2 } from 'lucide-vue-next';

const props = defineProps({
    profile: {
        type: Object,
        default: () => ({})
    },
    blocks: {
        type: Array,
        default: () => []
    },
    products: {
        type: Array,
        default: () => []
    },
    customTheme: {
        type:Object,
        default: null
    }
});

const emit = defineEmits(['block-click']);

// Theme Configuration Parser
const activeTheme = computed(() => {
    if (props.customTheme) return props.customTheme;
    if (props.profile?.themeConfigJson) {
        try {
            return JSON.parse(props.profile.themeConfigJson);
        } catch (e) {
            return {};
        }
    }
    return {
        bg: '#0f172a',
        primary: '#38bdf8',
        font: 'Inter',
        cardStyle: 'glass',
        buttonShape: 'rounded-xl'
    };
});

// Active Bio Blocks Filter
const activeBlocks = computed(() => {
    return (props.blocks || []).filter(b => b.isEnabled !== false);
});

// Dynamic Container Background
const screenContainerStyle = computed(() => {
    const bg = activeTheme.value.bg || '#0f172a';
    if (bg.startsWith('linear-gradient') || bg.startsWith('radial-gradient')) {
        return { background: bg };
    }
    return { backgroundColor: bg};
});

// Dynamic Button Shape
const buttonShapeClass = computed(() => {
    const shape = activeTheme.value.buttonShape || 'rounded-xl';
    switch (shape) {
        case 'rounded-full': return 'rounded-full';
        case 'rounded-none': return 'rounded-none';
        case 'rounded-md': return 'rounded-md';
        default: return 'rounded-xl';
    }
});

// Dynamic Button Styling Style
const buttonStyleClass = computed(() => {
    const style = activeTheme.value.cardStyle || 'glass';
    switch (style) {
        case 'solid': 
            return 'text-slate-950 shadow-md font-semibold hover:opacity-90';
        case 'outline':
            return 'bg-transparent border border-slate-600 text-slate-200 hover:border-slate-400';
        case 'neon':
            return 'bg-slate-950 border text-cyan-300 shadow-[0_0_12px_rgba(56,189,248,0.25)]';
        default: // 'glass'
            return 'bg-slate-800/60 backdrop-blur-md border border-white/10 text-slate-100 hover:bg-slate-800/80 shadow-sm';
    }
});

const buttonCustomStyle = computed(() => {
    const style = activeTheme.value.cardStyle || 'glass';
    const primary = activeTheme.value.primary || '#38bdf8';

    if (style === 'solid') {
        return { backgroundColor: primary };
    } else if (style === 'outline' || style === 'neon') {
        return { borderColor: primary };
    }
    return {};
});

const formatCurrency = (val) => {
    if (!val) return 'Rp 0';
    return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        maximumFractionDigits: 0
    }).format(val);
};

const handleBlockClick = (block) => {
    emit('block-click', block);
    if(block.url) {
        window.open(block.url, '_blank');
    }
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
</script>