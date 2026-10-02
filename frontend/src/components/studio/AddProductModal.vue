<template>
    <div v-if="modelValue" class="fixed inset-0 z-50 bg-black/75 backdrop-blur-sm flex items-center justify-center p-4">
        <div class="glass-card w-full max-w-lg p-6 rounded-3xl border border-slate-700/80 shadow-2xl space-y-4">
            <div class="flex items-center justify-between pb-2 border-b border-slate-800">
                <h3 class="text-sm font-bold text-white">
                    Create Protected Digital Product
                </h3>
                <button @click="close" class="text-slate-400 hover:text-white text-lg font-bold">&times;</button>
            </div>
            <form @submit.prevent="handleSubmit" class="space-y-4">
                <div>
                    <label class="block text-xs font-semibold text-slate-300 mb-1.5">
                        Product Title *
                    </label>
                    <input v-model="form.title" type="text" required placeholder="e.g. Master Guide to System Architecture"
                            class="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white focus:border-sky-500 focus:outline-none" />
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-300 mb-1.5">Description</label>
                    <textarea v-model="form.description" rows="2" placeholder="Brief description for buyers..."
                            class="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white focus:border-sky-500 focus:outline-none resize-none">
                    </textarea>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                        <label class="block text-xs font-semibold text-slate-300 mb-1.5">Price (IDR) *</label>
                        <input v-model.number="form.price" type="number" min="1000" step="1000" required placeholder="50000"
                                class="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white focus:border-sky-500 focus:outline-none" />
                    </div>
                    <div>
                        <label class="block text-xs font-semibold text-slate-300 mb-1.5">Cover Image URL (optional)</label>
                        <input v-model="form.coverImageUrl" type="url" placeholder="https://..."
                                class="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white focus:border-sky-500 focus:outline-none" />
                    </div>
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-300 mb-1.5">Master PDF File *</label>
                    <input type="file" accept=".pdf,application/pdf" required @change="onFileSelected"
                            class="w-full text-xs text-slate-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-xl file:border-0 file:text-xs file:font-semibold file:bg-slate-800 file:text-sky-400 hover:file:bg-slate-700" />
                    <p class="text-[11px] text-slate-500 mt-1">
                        Uploaded PDF is automatically stamped with buyer forensics upon purchase.
                    </p>
                </div>
                <div class="flex items-center gap-2">
                    <input id="drm-check" v-model="form.enableWatermark" type="checkbox"
                            class="rounded bg-slate-900 border-slate-700 text-sky-500 focus:ring-0" />
                    <label for="drm-check" class="text-xs text-slate-300 select-none">
                        Enable Anti-Piracy Watermarking (DRM)
                    </label>
                </div>
                <div class="flex items-center justify-end gap-2 pt-2">
                    <button type="button" @click="close"
                            class="px-4 py-2 rounded-xl text-xs font-semibold text-slate-400 hover:text-white">
                        Cancel    
                    </button>
                    <button type="submit" :disabled="submitting"
                            class="px-4 py-2 rounded-xl text-xs font-semibold bg-sky-500 text-slate-950 font-bold hover:opacity-90 disabled:opacity-50">
                        {{ submitting ? 'Uploading...' : 'Create Product' }}    
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
<script setup>
import { ref, reactive } from 'vue';
import { useCreatorStore } from '../../store/creator';

const props = defineProps({
    modelValue: { type: Boolean, default: false }
});
const emit = defineEmits(['update:modelValue']);

const creatorStore = useCreatorStore();
const submitting = ref(false);
const selectedFile = ref(null);

const form = reactive({
    title: '',
    description: '',
    price: 50000,
    coverImageUrl: '',
    enableWatermark: true
});

const onFileSelected = (e) => {
    if (e.target.files && e.target.files[0]) {
        selectedFile.value = e.target.files[0];
    }
};

const close = () => {
    emit('update:modelValue', false);
}

const handleSubmit = async () => {
    if (!selectedFile.value) {
        alert('Please select a PDF file');
        return;
    }
    submitting.value = true;
    try {
        const formData = new FormData();
        formData.append('title', form.title);
        formData.append('description', form.description || '');
        formData.append('price', form.price);
        if (form.coverImageUrl) {
            formData.append('coverImageUrl', form.coverImageUrl);
        }
        formData.append('enableWatermark', form.enableWatermark);
        formData.append('isPublished', true);
        formData.append('file', selectedFile.value);

        await creatorStore.createProduct(formData);
        form.title = '';
        form.description = ''
        form.price = 50000;
        form.coverImageUrl = ''
        selectedFile.value = null;
        close();
    } catch (err) {
        alert(err.response?.data?.message || 'Failed to create product');
    } finally {
        submitting.value = false;
    }
};
</script>