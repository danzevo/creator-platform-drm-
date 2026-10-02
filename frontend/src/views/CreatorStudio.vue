<template>
    <div class="min-h-screen flex flex-col bg-[#0b0f19] text-slate-100">
        <!-- Studio Navigation Bar -->
        <header class="h-16 px-6 glass-nav flex items-center justify-between shrink-0 sticky top-0 z-40">
            <div class="flex items-center gap-3">
                <router-link to="/" class="flex items-center gap-2.5">
                    <div class="w-8 h-8 rounded-lg bg-gradient-to-tr from-sky-500 to-cyan-400 p-0.5">
                        <div class="w-full h-full bg-[#0b0f19] rounded-[7px] flex items-center justify-center">
                            <ShieldCheck class="w-4 h-4 text-sky-400" />
                        </div>
                    </div>
                    <span class="text-sm font-extrabold text-white">Creator Studio</span>
                </router-link>
                <a :href="publicStorefrontUrl" target="_blank"
                    class="hidden sm:flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-800/80 hover:bg-slate-700 text-xs text-sky-400 border border-slate-700 transition-colors">
                    <span>/@{{ creatorStore.dashboard?.username }}</span>
                    <ExternalLink class="w-3 h-3" />
                </a>
            </div>
            <div class="flex items-center gap-3">
                <span class="text-xs text-slate-400 hidden sm:inline">
                    {{ authStore.user?.email }}
                </span>
                <button @click="handleLogout"
                        class="px-3 py-1.5 rounded-xl text-xs font-semibold bg-slate-800 hover:bg-rose-500/20 hover:text-rose-400 text-slate-300 transition-colors flex items-center gap-1.5">
                    <LogOut class="w-3.5 h-3.5" />
                    <span>Sign Out</span>
                </button>
            </div>
        </header>
        <!-- Studio Split Workspace -->
        <div class="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            <!-- Left Pane: Workstation Tabs (7 Cols) -->
            <div class="lg:col-span-7 space-y-6">
                <!-- Navigation Tabs Bar -->
                <div class="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none border-b border-slate-800">
                    <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id" 
                            :class="['px-3.5 py-2 rounded-xl text-xs font-semibold shrink-0 transition-all cursor-pointer flex items-center gap-2', activeTab === tab.id ? 'bg-sky-500 text-slate-950 shadow-md' : 'text-slate-400 hover:text-white hover:bg-slate-800/60']">
                        <component :is="tab.icon" class="w-4 h-4" />
                        <span>{{ tab.label }}</span>    
                    </button>
                </div>
                <!-- TAB 1: OVERVIEW & METRICS -->
                <div v-if="activeTab === 'overview'" class="space-y-6">
                    <!-- Metrics Cards -->
                    <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
                        <div class="glass-card p-4 rounded-2xl">
                            <span class="text-xs text-slate-400">Total Revenue</span>
                            <div class="text-base sm:text-lg font-bold text-emerald-400 mt-1">
                                {{ formatCurrency(creatorStore.dashboard?.totalRevenue) }}
                            </div>
                        </div>
                        <div class="glass-card p-4 rounded-2xl">
                            <span class="text-xs text-slate-400">Available Balance</span>
                            <div class="text-base sm:text-lg font-bold text-sky-400 mt-1">
                                {{ formatCurrency(creatorStore.dashboard?.availableBalance) }}
                            </div>
                        </div>
                        <div class="glass-card p-4 rounded-2xl">
                            <span class="text-xs text-slate-400">Total Orders</span>
                            <div class="text-base sm:text-lg font-bold text-white mt-1">
                                {{ creatorStore.dashboard?.totalOrdersCount || 0 }}
                            </div>
                        </div>
                        <div class="glass-card p-4 rounded-2xl">
                            <span class="text-xs text-slate-400">Products</span>
                            <div class="text-base sm:text-lg font-bold text-white mt-1">
                                {{ creatorStore.dashboard?.totalProductsCount || 0 }}
                            </div>
                        </div>
                    </div>
                    <!-- Quick Bio Share Link Box -->
                    <div class="glass-card p-5 rounded-2xl flex flex-col sm:flex-row items-center justify-between gap-4">
                        <div>
                            <h3 class="text-sm font-bold text-white">Your Public Storefront Link</h3>
                            <p class="text-xs text-slate-400 mt-0.5">
                                Share this in your Instagram or TikTok bio.
                            </p>
                        </div>
                        <div class="flex items-center gap-2 w-full sm:w-auto">
                            <input readonly :value="publicStorefrontUrl"
                                    class="w-full sm:w-60 px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-slate-300" />
                            <button @click="copyBioLink" class="px-3 py-2 rounded-xl bg-sky-500 text-slate-950 text-xs font-bold shrink-0 hover:opacity-90">
                                {{ copiedLink ? 'Copied' : 'Copy' }}
                            </button>
                        </div>
                    </div>
                </div>
                <!-- TAB 2: BIO LINKS MANAGER -->
                <div v-else-if="activeTab === 'links'" class="space-y-4">
                    <div class="flex items-center justify-between">
                        <h2 class="text-sm font-bold text-white">Bio Links & Headers</h2>
                        <button @click="showAddBlockModal = true"
                                class="px-3.5 py-1.5 rounded-xl text-xs font-semibold bg-sky-500 text-slate-950 flex items-center gap-1.5 hover:opacity-90">
                            <Plus class="w-4 h-4" />
                            <span>Add Block</span>
                        </button>
                    </div>
                    <div v-if="creatorStore.blocks.length === 0" class="p-8 text-center glass-card rounded-2xl text-xs text-slate-500">
                        No links added yet. Click "Add Block" above.
                    </div>
                    <div v-else class="space-y-2.5">
                        <div v-for="(block, index) in creatorStore.blocks" :key="block.id" draggable="true"
                            @dragstart="onDragStart(index)"
                            @dragover.prevent
                            @dragenter.prevent="onDragEnter(index)"
                            @dragend="onDragEnd"
                            :class="['glass-card p-4 rounded-2xl flex items-center justify-between gap-3 transition-all duration-200 cursor-default', draggedIndex === index ? 'opacity-40 scale-95 border-sky-500 border-dashed' : 'hover:border-slate-700']">
                            <div class="flex items-center gap-3 min-w-0">
                                <GripVertical class="w-4 h-4 text-slate-600 shrink-0 cursor-grab active:cursor-grabbing" />
                                <div class="min-w-0">
                                    <div class="flex items-center gap-2">
                                        <span v-if="block.blockType === 'HEADER'" class="px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-500/10 text-amber-400 border border-amber-500/20">
                                            HEADER
                                        </span>
                                        <span v-else-if="block.blockType === 'SOCIALS'" class="px-1.5 py-0.5 rounded text-[10px] font-bold bg-purple-500/10 text-purple-400 border border-purple-500/20">
                                            SOCIAL
                                        </span>
                                        <h4 class="text-sm font-bold text-white truncate">{{ block.title }}</h4>
                                    </div>
                                    <p v-if="block.url" class="text-xs text-slate-400 font-mono truncate">
                                        {{ block.url }}
                                    </p>
                                </div>
                            </div>
                            <button @click="deleteBlock(block.id)" class="p-2 text-slate-500 hover:text-rose-400 rounded-lg hover:bg-rose-500/10 transition-colors">
                                <Trash2 class="w-4 h-4" />
                            </button>
                        </div>
                    </div>
                </div>
                <!-- TAB 3: DIGITAL PRODUCTS -->
                <div v-else-if="activeTab === 'products'" class="space-y-4">
                    <div class="flex items-center justify-between">
                        <div>
                            <h2 class="text-sm font-bold text-white">Protected Digital Products</h2>
                            <p class="text-xs text-slate-400">PDF E-books with automated buyer watermarking</p>
                        </div>
                        <button @click="showAddProductModal = true" class="px-3.5 py-1.5 rounded-xl text-xs font-semibold bg-sky-500 text-slate-950 flex items-center gap-1.5 hover:opacity-90">
                            <Plus class="w-4 h-4" />
                            <span>New Product</span>
                        </button>
                    </div>
                    <div v-if="creatorStore.products.length === 0" class="p-8 text-center glass-card rounded-2xl text-xs text-slate-500">
                        No digital products uploaded yet.
                    </div>
                    <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div v-for="product in creatorStore.products" :key="product.id"
                            class="glass-card p-4 rounded-2xl flex flex-col justify-between">
                            <div>
                                <div class="flex items-center justify-between mb-2">
                                    <span class="text-xs font-bold text-sky-400">{{ formatCurrency(product.price) }}</span>
                                    <span class="px-2 py-0.5 rounded-full text-[9px] font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                                        DRM Active
                                    </span>
                                </div>
                                <h4 class="text-sm font-bold text-white">{{ product.title }}</h4>
                                <p class="text-xs text-slate-400 mt-1 line-clamp-2">{{ product.description }}</p>
                            </div>
                            <div class="mt-4 pt-3 border-t border-slate-800 flex justify-end">
                                <button @click="deleteProduct(product.id)" class="text-xs text-rose-400 hover:text-rose-300 flex items-center gap-1">
                                    <Trash2 class="w-3.5 h-3.5" />
                                    Delete
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- TAB 4: THEME & CUSTOMIZATION -->
                <div v-else-if="activeTab === 'theme'" class="glass-card p-6 rounded-3xl space-y-6">
                    <h2 class="text-sm font-bold text-white">
                        Bio Theme Customization
                    </h2>
                    <div>
                        <label class="block text-xs font-semibold text-slate-300 mb-2">
                            Background Color / Gradient
                        </label>
                        <div class="grid grid-cols-4 gap-2">
                            <button v-for="bg in presetBgs" :key="bg.value" @click="themeForm.bg = bg.value;saveTheme()"
                                    :style="{ background: bg.value }" :class="['h-10 rounded-xl border-2 transition-all', themeForm.bg === bg.value ? 'border-sky-400 scale-105' : 'border-transparent']" />
                        </div>
                    </div>
                    <div>
                        <label class="block text-xs font-semibold text-slate-300 mb-2">
                            Primary Button Style
                        </label>
                        <div class="grid grid-cols-4 gap-2">
                            <button v-for="style in ['glass', 'solid', 'outline', 'neon']" :key="style"
                                    @click="themeForm.cardStyle = style;saveTheme()"
                                    :class="['py-2 rounded-xl text-xs capitalize font-semibold border transition-all', themeForm.cardStyle === style ? 'border-sky-400 bg-sky-500/10 text-white' : 'border-slate-800 text-slate-400']">
                                {{ style }}    
                            </button>
                        </div>
                    </div>
                </div>
                <!-- TAB 5: ORDERS -->
                <div v-else-if="activeTab === 'orders'" class="glass-card rounded-3xl p-5 space-y-4">
                    <h2 class="text-sm font-bold text-white">Sales & Orders Ledger</h2>
                    <div v-if="creatorStore.orders.length === 0" class="text-xs text-slate-500 py-6 text-center">
                        No orders recorded yet.
                    </div>
                    <div v-else class="overflow-x-auto">
                        <table class="w-full text-left text-xs">
                            <thead class="text-slate-500 border-b border-slate-800">
                                <tr>
                                    <th class="py-2.5">Buyer</th>
                                    <th>Product</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody class="divide-y divide-slate-800/60 text-slate-300">
                                <tr v-for="order in creatorStore.orders" :key="order.orderId">
                                    <td class="py-3 font-mono">{{ order.buyerEmail }}</td>
                                    <td>{{ order.productTitle }}</td>
                                    <td class="font-bold text-white">{{ formatCurrency(order.totalAmount) }}</td>
                                    <td>
                                        <span :class="['px-2 py-0.5 rounded-full text-[10px] font-semibold', order.status === 'PAID' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-amber-500/10 text-amber-400 border border-amber-500/20']">
                                            {{ order.status }}
                                        </span>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <!-- Right Pane: Sticky Live Mobile Mockup (5 Cols) -->
            <div class="lg:col-span-5 sticky top-24 hidden lg:flex flex-col items-center">
                <span class="text-xs font-semibold text-slate-500 uppercase tracking-widest mb-3">Live Bio Preview</span>
                <MobileMockup :profile="creatorStore.dashboard" :blocks="creatorStore.blocks" :products="creatorStore.products" :customTheme="themeForm" />
            </div>
        </div>
        <!-- Modal: Add Block -->
        <AddBlockModal v-model="showAddBlockModal" />

        <!-- Modal: Add Digital Product -->
        <AddProductModal v-model="showAddProductModal" />
    </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { useCreatorStore } from '../store/creator';
import MobileMockup from '../components/studio/MobileMockup.vue';
import { ShieldCheck, ExternalLink, LogOut, LayoutDashboard, Link as LinkIcon, ShoppingBag, Palette, DollarSign, Plus, Trash2, GripVertical} from 'lucide-vue-next';
import AddBlockModal from '../components/studio/AddBlockModal.vue';
import AddProductModal from '../components/studio/AddProductModal.vue';

const router = useRouter();
const authStore = useAuthStore();
const creatorStore = useCreatorStore();

const activeTab = ref('overview');
const copiedLink = ref(false);
const draggedIndex = ref(null);
const showAddBlockModal = ref(false);
const showAddProductModal = ref(false);

const onDragStart = (index) => {
    draggedIndex.value = index;
}

const onDragEnter = (targetIndex) => {
    if (draggedIndex.value === null || draggedIndex.value === targetIndex) return;

    // Move dragged item locally for instant visual feedback
    const items = [...creatorStore.blocks];
    const [movedItem] = items.splice(draggedIndex.value, 1);
    items.splice(targetIndex, 0, movedItem);
    creatorStore.blocks = items;
    draggedIndex.value = targetIndex;
}

const onDragEnd = async() => {
    draggedIndex.value = null;

    // Sync new sort order with backend
    try {
        const payload = creatorStore.blocks.map((block, index) => ({
            id: block.id,
            sortOrder: index
        }));
        await creatorStore.reorderBlocks(payload);
    } catch (err) {
        console.error('Failed to save reorder blocks:', err);
    }
};

const tabs = [
    { id: 'overview', label: 'Overview', icon: LayoutDashboard },
    { id: 'links', label: 'Bio Links', icon: LinkIcon },
    { id: 'products', label: 'Products', icon:ShoppingBag },
    { id: 'theme', label: 'Theme', icon: Palette },
    { id: 'orders', label: 'Orders', icon: DollarSign }
];

const presetBgs = [
    { value: '#0b0f19' },
    { value: '#0f172a' },
    { value: 'linear-gradient(135deg, #0b0f19 0%, #1e1b4b 100%)'},
    { value: 'linear-gradient(135deg, #0f172a 0%, #064e3b 100%)'}
];

const themeForm = reactive({
    bg: '#0b0f19',
    cardStyle: 'glass',
    buttonShape: 'rounded-xl',
    primary: '#38bdf8'
});

const publicStorefrontUrl = computed(() => {
    return `${window.location.origin}/@${creatorStore.dashboard?.username || 'creator'}`;
});

const formatCurrency = (val) => {
    if (!val) return 'Rp 0';
    return new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0}).format(val);
};

const copyBioLink = () => {
    navigator.clipboard.writeText(publicStorefrontUrl.value);
    copiedLink.value = true;
    setTimeout(() => {
        copiedLink.value = false;
    }, 2000);
};

const deleteBlock = async(id) => {
    if (confirm('Delete this bio block?')) {
        await creatorStore.deleteBlock(id);
    }
};

const deleteProduct = async (id) => {
    if (confirm('Delete this digital product?')) {
        await creatorStore.deleteProduct(id);
    }
};

const saveTheme = async () => {
    await creatorStore.updateTheme(themeForm);
};

const handleLogout = () => {
    authStore.logout();
    router.push('/');
};

onMounted(async () => {
    await creatorStore.fetchAll();
    if (creatorStore.dashboard?.themeConfigJson) {
        try {
            const parsed = JSON.parse(creatorStore.dashboard.themeConfigJson);
            Object.assign(themeForm, parsed);
        } catch (e) {}
    }
});
</script>