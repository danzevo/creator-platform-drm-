<template>
    <footer class="border-t border-slate-800/80 bg-[#090d16] text-slate-400 text-sm py-12 mt-auto">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-10">
                <div class="md:col-span-2 space-y-4">
                    <div class="flex items-center space-x-3">
                        <div class="w-8 h-8 rounded-lg bg-sky-500/10 border border-sky-500/20 flex items-center justify-center">
                            <ShieldCheck class="w-4 h-4 text-sky-400" />
                        </div>
                        <span class="text-base font-bold font-display text-white">
                            CreatorLock
                        </span>
                    </div>
                    <p class="text-slate-400 text-sm max-w-sm leading-relaxed">
                        The high-converting bio-storefront built for digital creators. Protect your intellectual property with military-grade buyer watermarking and zero-leak canvas viewing.
                    </p>
                    <div class="flex items-center gap-3 text-xs text-slate-500 pt-1">
                        <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                            <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
                            PyMuPDF Dynamic DRM Engine Active
                        </span>
                    </div>
                </div>
                <div>
                    <h4 class="text-xs font-semibold uppercase tracking-wider text-slate-200 mb-3">
                        Product
                    </h4>
                    <ul class="space-y-2 text-sm">
                        <li><a href="#features" class="hover:text-sky-400 transition-colors">Bio Storefront</a></li>
                        <li><a href="#anti-piracy" class="hover:text-sky-400 transition-colors">Anti-Piracy DRM</a></li>
                        <li><a href="#pricing" class="hover:text-sky-400 transition-colors">Payment & QRIS</a></li>
                        <li><router-link to="/login" class="hover:text-sky-400 transition-colors">Creator Studio</router-link></li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-xs font-semibold uppercase tracking-wider text-slate-200 mb-3">Legal & Security</h4>
                    <ul class="space-y-2 text-sm">
                        <li><a href="#" class="hover:text-sky-400 transition-colors">Terms of Service</a></li>
                        <li><a href="#" class="hover:text-sky-400 transition-colors">Privacy Policy</a></li>
                        <li><a href="#" class="hover:text-sky-400 transition-colors">Security</a></li>
                        <li><a href="#" class="hover:text-sky-400 transition-colors">Report Piracy</a></li>
                    </ul>
                </div>
            </div>
            <div class="pt-8 border-t border-slate-800/60 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-4">
                <p>© {{  new Date().getFullYear() }} CreatorLock Platform. All rights reserved.</p>
                <p class="flex items-center gap-1 text-slate-400">
                    Crafted for Indonesian & Global Digital Creators
                </p>
            </div>
        </div>
    </footer>
</template>
<script setup>
import { ShieldCheck } from 'lucide-vue-next';
</script>