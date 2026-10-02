<template>
    <div class="min-h-screen flex flex-col bg-[#0b0f19] text-slate-100 glow-mesh">
        <Navbar />

        <!-- Hero Section -->
        <section class="relative pt-20 pb-28 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto flex flex-col items-center text-center">
            <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-sky-500/10 border border-sky-500/20 text-sky-400 text-xs font-semibold uppercase tracking-wider mb-6 animate-pulse">
                <ShieldCheck class="w-4 h-4" />
                Next-Gen Anti-Piracy Creator Platform
            </div>
            <h1 class="text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight font-display max-w-4xl leading-[1.1]">
                Monetize Your Audience.
                <span class="bg-gradient-to-r from-sky-400 via-cyan-300 to-indigo-400 bg-clip-text text-transparent">
                    Eliminate Digital Piracy.
                </span>
            </h1>
            <p class="mt-6 text-base sm:text-xl text-slate-300 max-w-2xl font-normal leading-relaxed">
                The high-converting link-in-bio storefront equipped with indelible buyer watermarking, protected in-app canvas viewing, and direct Indonesian QRIS payouts.
            </p>
            <!-- CTA Form & Buttons -->
            <div class="mt-10 flex flex-col sm:flex-row items-center gap-4 w-full max-w-md">
                <div class="relative w-full">
                    <span class="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-mono text-sm">lumenbio.me/@</span>
                    <input v-model="usernameClaim" type="text" placeholder="yourname"
                            class="w-full pl-32 pr-4 py-3.5 rounded-2xl glass-input text-sm font-medium focus:ring-2 focus:ring-sky-500" />
                </div>
                <button @click="handleClaimUsername"
                        class="w-full sm:w-auto px-7 py-3.5 rounded-2xl font-bold text-sm bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 hover:opacity-95 shadow-lg shadow-sky-500/25 transition-all flex items-center justify-center gap-2 shrink-0 cursor-pointer">
                    <span>Claim Link</span>
                    <ArrowRight class="w-4 h-4" />
                </button>
            </div>
            <!-- Trust Badges -->
            <div class="mt-12 grid grid-cols-3 gap-6 sm:gap-12 border-t border-slate-800/80 pt-8 text-slate-400 text-xs sm:text-sm">
                <div>
                    <div class="text-2xl font-extrabold text-white">99.8%</div>
                    <div class="mt-0.5 text-slate-400">Leak Difference</div>
                </div>
                <div>
                    <div class="text-2xl font-extrabold text-white">&lt; 1.2s</div>
                    <div class="mt-0.5 text-slate-400">PyMuPDF Stamping</div>
                </div>
                <div>
                    <div class="text-2xl font-extrabold text-white">100%</div>
                    <div class="mt-0.5 text-slate-400">Direct QRIS Payout</div>
                </div>
            </div>
        </section>
        <!-- Anti-Piracy DRM Workflow Section -->
        <section id="anti-piracy" class="py-24 border-t border-slate-800/80 bg-slate-950/50">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="text-center max-w-2xl mx-auto mb-16">
                    <h2 class="text-xs font-bold uppercase tracking-widest text-cyan-400 mb-2">
                        Military-Grade IP Protection
                    </h2>
                    <p class="text-3xl font-extrabold text-white font-display">
                        How LumenBio Eliminates E-Book Piracy
                    </p>
                    <p class="text-slate-400 text-sm mt-3">
                        Every digital product purchase goes through an automated forensic stamping pipeline before buyer access is granted.
                    </p>
                </div>
                <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                    <!-- Step 1 -->
                    <div class="glass-card-hover p-8 rounded-3xl relative overflow-hidden group">
                        <div class="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/20 text-sky-400 flex items-center justify-center mb-6">
                            <UploadCloud class="w-6 h-6" />
                        </div>
                        <h3 class="text-lg font-bold text-white mb-2">1. Secure Master Vault</h3>
                        <p class="text-slate-400 text-sm leading-relaxed">
                            Creators upload their raw PDF e-books to an isolated private storage bucket. Raw files are never exposed publicly or directly linked.
                        </p>
                    </div>
                    <!-- Step 2 -->
                    <div class="glass-card-hover p-8 rounded-3xl relative overflow-hidden group border-sky-500/30">
                        <div class="w-12 h-12 rounded-2xl bg-cyan-500/10 border border-cyan-500/20 text-cyan-400 flex items-center justify-center mb-6">
                            <Cpu class="w-6 h-6" />
                        </div>
                        <h3 class="text-lg font-bold text-white mb-2">
                            2. Dynamic PyMuPDF Stamping
                        </h3>
                        <p class="text-slate-400 text-sm leading-relaxed">
                            Upon verified payment, Python PyMuPDF stamps the buyer's email, phone, and order token across every page diagonally and into a security banner.
                        </p>
                    </div>
                    <!-- Step 3 -->
                    <div class="glass-card-hover p-8 rounded-3xl relative overflow-hidden group">
                        <div class="w-12 h-12 rounded-2xl bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 flex items-center justify-center mb-6">
                            <ShieldAlert class="w-6 h-6" />
                        </div>
                        <h3 class="text-lg font-bold text-white mb-2">3. Protected Canvas Reader</h3>
                        <p class="text-slate-400 text-sm leading-relaxed">
                            Documents render strictly onto HTML5 vector canvas. Right-click, Ctrl+P printing, copy-paste, and tab unfocus are actively intercepted.
                        </p>
                    </div>
                </div>
            </div>
        </section>
        <!-- Feature Comparison Matrix -->
        <section id="comparison" class="py-24 border-t border-slate-800/80">
            <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="text-center mb-16">
                    <h2 class="text-xs font-bold uppercase tracking-widest text-cyan-400 mb-2">
                        Side-by-Side Comparison
                    </h2>
                    <p class="text-3xl font-extrabold text-white font-display">
                        Why Top Creators Choose LumenBio
                    </p>
                </div>
                <div class="glass-card rounded-3xl overflow-hidden border border-slate-800">
                    <div class="overflow-x-auto">
                        <table class="w-full text-left text-sm">
                            <thead class="bg-slate-900/80 text-xs uppercase tracking-wider text-slate-400 border-b border-slate-800">
                                <tr>
                                    <th class="py-4 px-6">Feature</th>
                                    <th class="py-4 px-6 text-sky-400 font-bold">LumenBio</th>
                                    <th class="py-4 px-6 text-slate-400">Lynk.id</th>
                                    <th class="py-4 px-6 text-slate-400">Gumroad</th>
                                </tr>
                            </thead>
                            <tbody class="divide-y divide-slate-800/60 text-slate-300">
                                <tr>
                                    <td class="py-4 px-6 font-semibold text-white">Indelible Dynamic Watermarking</td>
                                    <td class="py-4 px-6 text-emerald-400 font-semibold flex items-center gap-1.5">
                                        <Check class="w-4 h-4" />
                                        Real-Time Python Engine
                                    </td>
                                    <td class="py-4 px-6 text-slate-500">❌ None</td>
                                    <td class="py-4 px-6 text-slate-400">⚠️ Basic 1-line stamp</td>
                                </tr>
                                <tr>
                                    <td class="py-4 px-6 font-semibold text-white">Protected In-App Canvas Reader</td>
                                    <td class="py-4 px-6 text-emerald-400 font-semibold flex items-center gap-1.5">
                                        <Check class="w-4 h-4" />
                                        Anti-Print & Blur Shield
                                    </td>
                                    <td class="py-4 px-6 text-slate-500">❌ Direct PDF Download</td>
                                    <td class="py-4 px-6 text-slate-500">❌ Direct PDF Download</td>
                                </tr>
                                <tr>
                                    <td class="py-4 px-6 font-semibold text-white">Indonesian Direct QRIS</td>
                                    <td class="py-4 px-6 text-emerald-400 font-semibold flex items-center gap-1.5">
                                        <Check class="w-4 h-4" />
                                        Native Instant Webhook
                                    </td>
                                    <td class="py-4 px-6 text-emerald-400 font-semibold flex items-center gap-1.5">
                                        <Check class="w-4 h-4" />
                                        Supported
                                    </td>
                                    <td class="py-4 px-6 text-slate-500">
                                        ❌ Credit Card / PayPal only
                                    </td>
                                </tr>
                                <tr>
                                    <td class="py-4 px-6 font-semibold text-white">
                                        Live Mobile Mockup Studio
                                    </td>
                                    <td class="py-4 px-6 text-emerald-400 font-semibold flex items-center gap-1.5">
                                        <Check class="w-4 h-4" />
                                        Instant Split-Screen
                                    </td>
                                    <td class="py-4 px-6 text-slate-400">⚠️ Partial preview</td>
                                    <td class="py-4 px-6 text-slate-500">❌ Standard form</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </section>
        <!-- Onboarding CTA -->
        <section class="py-20 border-t border-slate-800/80 bg-gradient-to-b from-transparent to-sky-950/20 text-center">
            <div class="max-w-3xl mx-auto px-4">
                <h2 class="text-3xl sm:text-4xl font-extrabold text-white font-display">
                    Ready to Protect Your Digital Revenue?
                </h2>
                <p class="text-slate-400 text-sm sm:text-base mt-3">
                    Join creators who safeguard their courses, guides, and digital books with LumenBio.
                </p>
                <div class="mt-8 flex justify-center">
                    <router-link to="/register"
                                class="px-8 py-4 rounded-2xl font-bold text-sm bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 hover:opacity-95 shadow-xl shadow-sky-500/25 transition-all flex items-center gap-2">
                        <span>Get Started Free in 30 Seconds</span>
                        <ArrowRight class="w-4 h-4" />
                    </router-link>
                </div>
            </div>
        </section>
        <Footer />
    </div>
</template>
<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import Navbar from '../components/common/Navbar.vue';
import Footer from '../components/common/Footer.vue';
import { ShieldCheck, ArrowRight, UploadCloud, Cpu, ShieldAlert, Check } from 'lucide-vue-next';

const router = useRouter();
const usernameClaim = ref('');

const handleClaimUsername = () => {
    const trimmed = usernameClaim.value.trim().replace(/^@/, '');
    if (trimmed) {
        router.push({ path: '/register', query: { username: trimmed }});
    } else {
        router.push('/register');
    }
}
</script>