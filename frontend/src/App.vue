<template>
    <div class="min-h-screen bg-[#0b0f19] text-slate-100 flex flex-col font-sans">
        <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
                <component :is="Component"/>
            </transition>
        </router-view>
    </div>
</template>
<script setup>
import { onMounted } from 'vue';
import { useAuthStore } from './store/auth';

const authStore = useAuthStore();

onMounted(async() => {
    if (authStore.token) {
        await authStore.fetchMe();
    }
})
</script>

<style>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>