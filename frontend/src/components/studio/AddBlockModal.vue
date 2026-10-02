<template>    
    <div v-if="modelValue" class="fixed inset-0 z-50 bg-black/75 backdrop-blur-sm flex items-center justify-center p-4">
        <div class="glass-card w-full max-w-md p-6 rounded-3xl border border-slate-700/80 shadow-2xl space-y-4">
            <div class="flex items-center justify-between pb-2 border-b border-slate-800">
                <h3 class="text-sm font-bold text-white">
                    Add Bio Link or Header
                </h3>
                <button @click="close" class="text-slate-400 hover:text-white text-lg font-bold">&times;</button>
            </div>
            <form @submit.prevent="handleSubmit" class="space-y-4">
                <div>
                    <label class="block text-xs font-semibold text-slate-300 mb-1.5">Block Type</label>
                    <select v-model="form.blockType" class="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white focus:border-sky-500 focus:outline-none">
                        <option value="LINK">Link Block</option>
                        <option value="HEADER">Section Header</option>
                        <option value="SOCIALS">Socials Link</option>
                    </select>
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-300 mb-1.5">Title / Label *</label>
                    <input v-model="form.title" type="text" required placeholder="e.g. My Portfolio"
                            class="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white focus:border-sky-500 focus:outline-none" />
                </div>
                <div v-if="form.blockType !== 'HEADER'">
                    <label class="block text-xs font-semibold text-slate-300 mb-1.5">Destination URL *</label>
                    <input v-model="form.url" type="url" required placeholder="https://..."
                            class="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white focus:border-sky-500 focus:outline-none" />
                </div>
                <div class="flex items-center justify-end gap-2 pt-2">
                    <button type="button" @click="close"
                            class="px-4 py-2 rounded-xl text-xs font-semibold text-slate-400 hover:text-white">
                        Cancel
                    </button>
                    <button type="submit" :disabled="submitting"
                            class="px-4 py-2 rounded-xl text-xs font-semibold bg-sky-500 text-slate-950 font-bold hover:opacity-90 disabled:opacity-50">
                        {{ submitting ? 'Saving...' : 'Add Block' }}
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

const form = reactive({
    title: '',
    url: '',
    blockType: 'LINK'
});

const close = () => {
    emit('update:modelValue', false);
}

const handleSubmit = async () => {
    submitting.value = true;
    try {
        await creatorStore.createBlock({
            title: form.title,
            url: form.url,
            blockType: form.blockType,
            sortOrder: creatorStore.blocks.length
        });
        form.title = '';
        form.url = '';
        form.blockType = 'LINK';
        close();
    } catch (err) {
        alert(err.response?.data?.message || 'Failed to add block');
    } finally {
        submitting.value = false;
    }
}
</script>