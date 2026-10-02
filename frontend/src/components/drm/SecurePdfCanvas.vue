<template>
    <div class="flex flex-col h-full select-none drm-canvas-container" @contextmenu.prevent>
        <!-- Top Control Bar -->
        <div class="flex items-center justify-between px-4 py-3 bg-slate-900/90 border-b border-slate-800/80 backdrop-blur-md shrink-0">
            <!-- Page Navigation -->
            <div class="flex items-center gap-2">
                <button @click="prevPage" :disabled="currentPage <= 1 || loading"
                        class="p-1.5 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                    <ChevronLeft class="w-5 h-5" />   
                </button>
                <div class="text-xs font-semibold text-slate-300">
                    <span>Page {{ currentPage }}</span>
                    <span class="text-slate-500 mx-1">/</span>
                    <span>{{ totalPages || 1 }}</span>
                </div>
                <button @click="nextPage" :disabled="currentPage >= totalPages || loading"
                        class="p-1.5 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800 disabled:opacity-30 disabled:cursor-not-allowed transition-colors">
                    <ChevronRight class="w-5 h-5" />    
                </button>
            </div>
            <!-- Zoom & Fit Controls -->
            <div class="flex items-center gap-2">
                <button @click="zoomOut" :disabled="scale <= 0.75 || loading"
                        class="p-1.5 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800 disabled:opacity-30 transition-colors" title="Zoom Out">
                    <ZoomOut class="w-4 h-4" />
                </button>
                <span class="text-xs font-mono text-slate-400 min-w-[48px] text-center">
                    {{ Math.round(scale * 100) }}%
                </span>
                <button @click="zoomIn" :disabled="scale >= 2.5 || loading"
                        class="p-1.5 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800 disabled:opacity-30 transition-colors" title="Zoom In">
                    <ZoomIn class="w-4 h-4" />
                </button>
                <button @click="fitToWidth" :disabled="loading"
                        class="p-1.5 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800 disabled:opacity-30 transition-colors" title="Fit to Screen">
                    <Maximize2 class="w-4 h-4" />
                </button>
            </div>
        </div>
        <!-- Canvas Viewport Scroll Area -->
        <div ref="containerRef" class="flex-1 overflow-auto p-4 sm:p-8 flex items-center justify-center bg-slate-950/90 relative">
            <div v-if="loading" class="flex flex-col items-center gap-3 text-sky-400">
                <Loader2 class="w-8 h-8 animate-spin" />
                <span class="text-xs font-medium text-slate-300">
                    Decrypting & rendering vector document...
                </span>
            </div>
            <!-- Error State -->
            <div v-else-if="error" class="text-center p-6 glass-card rounded-2xl max-w-sm">
                <AlertCircle class="w-8 h-8 text-rose-400 mx-auto mb-2" />
                <p class="text-sm font-semibold text-white mb-1">Failed to load document</p>
                <p class="text-xs text-slate-400 mb-4">{{ error }}</p>
                <button @click="loadPdf" class="px-4 py-2 rounded-xl text-xs font-semibold bg-sky-500 text-slate-950 hover:bg-sky-400 transition-colors">
                    Try Again
                </button>
            </div>
            <!-- Canvas Element -->
            <div v-show="!loading && !error" class="relative shadow-2xl rounded-lg overflow-hidden border border-slate-800 bg-white">
                <canvas ref="canvasRef" class="block"></canvas>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue';
import * as pdfjsLib from 'pdfjs-dist';
import { ChevronLeft, ChevronRight, ZoomIn, ZoomOut, Maximize2, Loader2, AlertCircle } from 'lucide-vue-next';

// Set up PDF.js worker
pdfjsLib.GlobalWorkerOptions.workerSrc = `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.js`;

const props = defineProps({
    pdfUrl: { type: String, required:true },
    buyerEmail: { type: String, default: '' },
    orderId: { type: String, default: '' }
});

const containerRef = ref(null);
const canvasRef = ref(null);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(1);
const totalPages = ref(1);
const scale = ref(1.2);

let pdfDoc = null;
let currentRenderTask = null;

const loadPdf = async () => {
    loading.value = true;
    error.value = null;

    try {
        if (currentRenderTask) {
            currentRenderTask.cancel();
        }

        const loadingTask = pdfjsLib.getDocument({
            url: props.pdfUrl,
            withCredentials: true
        });

        pdfDoc = await loadingTask.promise;
        totalPages.value = pdfDoc.numPages;
        currentPage.value = 1;
        await renderPage(currentPage.value);
    } catch (err) {
        console.error('PDF loading error:', err);
        error.value = err.message || 'Unable to fetch protected document stream.';
    } finally {
        loading.value = false;
    }
};

const renderPage = async (pageNumber) => {
    if(!pdfDoc || !canvasRef.value) return;

    try {
        if (currentRenderTask) {
            currentRenderTask.cancel();
        }

        const page = await pdfDoc.getPage(pageNumber);
        const viewport = page.getViewport({ scale: scale.value });

        const canvas = canvasRef.value;
        const ctx = canvas.getContext('2d');

        canvas.height = viewport.height;
        canvas.width = viewport.width;

        const renderContext = {
            canvasContext: ctx,
            viewport: viewport
        };

        currentRenderTask = page.render(renderContext);
        await currentRenderTask.promise;

        // Apply dynamic canvas-level watermark overlay over the rendered pixels
        // applyCanvasWatermark(ctx, viewport.width, viewport.height);
    } catch (err) {
        if (err.name !== 'RenderingCancelledException') {
            console.error('Render page error:', err);
        }
    }
};

const applyCanvasWatermark = (ctx, width, height) => {
    if (!props.buyerEmail && !props.orderId) return;

    ctx.save();
    ctx.font = 'bold 15px sans-serif';
    ctx.fillStyle = 'rgba(100, 116, 139, 0.18)'; // Semi-transparent overlay
    ctx.rotate((-35 * Math.PI) / 180);

    const text = `CONFIDENTIAL • LICENSED TO: ${props.buyerEmail} • ORDER #${props.orderId?.substring(0, 8)}`;

    // Draw repeating diagonal stamp lines across the canvas
    for (let x = -width; x < width * 2; x += 300) {
        for (let y = -height; y < height * 2; y+= 180) {
            ctx.fillText(text, x, y);
        }
    }

    ctx.restore();
};

const nextPage = async () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++;
        await renderPage(currentPage.value);
    }
};

const prevPage = async () => {
    if (currentPage.value > 1) {
        currentPage.value--;
        await renderPage(currentPage.value);
    }
};

const zoomIn = async () => {
    scale.value = Math.min(2.5, scale.value + 0.2);
    await renderPage(currentPage.value);
}

const zoomOut = async () => {
    scale.value = Math.max(0.75, scale.value - 0.2);
    await renderPage(currentPage.value);
};

const fitToWidth = async () => {
    if (!containerRef.value || !pdfDoc) return;
    const page = await pdfDoc.getPage(currentPage.value);
    const unscaledViewport = page.getViewport({ scale: 1.0 });
    const availableWidth = containerRef.value.clientWidth - 80;
    scale.value = Math.max(0.8, Math.min(2.0, availableWidth / unscaledViewport.width));
    await renderPage(currentPage.value);
}

watch(() => props.pdfUrl, () => {
    if (props.pdfUrl) loadPdf();
});

onMounted(() => {
    if (props.pdfUrl) loadPdf();
})
</script>

<style scoped>
.drm-canvas-container {
    -webkit-user-select: none;
    user-select: none;
}
</style>