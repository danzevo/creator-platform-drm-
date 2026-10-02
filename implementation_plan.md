# Complete Implementation Plan: MVP 1 & MVP 2 Creator Platform

A decoupled modular creator platform combining Lynk.id's baseline features with 5 key differentiators across two strategic MVP phases.

---

## Technical Stack

| Layer | Technology | Key Libraries |
| :--- | :--- | :--- |
| **Frontend** | **Vue 3 + Vite** | • **TailwindCSS** (styling)<br>• **Pinia** (state management)<br>• **Vue Router**<br>• **VueDraggable / GridStack** (MVP 2 Drag-and-Drop)<br>• **PDF.js Canvas** (DRM reader)<br>• **Video.js / Hls.js** (MVP 2 Video player) |
| **Backend Core** | **Java Spring Boot 3** | • **Project Lombok** (`@Data`, `@Builder`, `@RequiredArgsConstructor`)<br>• **Spring Data JPA & PostgreSQL**<br>• **Spring Security & JJWT** (Auth & RBAC)<br>• **Spring Data Redis** (Queue & Pub/Sub)<br>• **me.paulschwarz:springboot-dotenv** (`.env` config reader) |
| **DRM & Media Worker** | **Python 3.11+** | • **SQLModel** (ORM matching PostgreSQL)<br>• **PyMuPDF (`fitz`)** (PDF watermarking)<br>• **FFmpeg-python** (MVP 2 Video HLS & AES-128 encryption)<br>• **FastAPI / Redis Worker** (Task consumer)<br>• **python-dotenv** (`.env` reader) |

---

## Project Structure (All Phases)

```
creator-platform/
├── .env.example
├── docker-compose.yml
│
├── frontend/                        # Vue 3 + TailwindCSS
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/              # Navbar, Modal, Button
│   │   │   ├── studio/              # MobileMockup, ThemeSelector, LinkBlockEditor
│   │   │   ├── grid/                # [MVP 2] 12-Column Drag-and-Drop Grid Canvas
│   │   │   ├── widgets/             # [MVP 2] CalculatorWidget, PollWidget, LeadFormWidget
│   │   │   └── drm/                 # SecurePdfCanvas, [MVP 2] SecureVideoPlayer
│   │   ├── views/
│   │   │   ├── LandingPage.vue      # SaaS Marketing Homepage
│   │   │   ├── BioStorefront.vue    # Public Creator Bio-link (@username)
│   │   │   ├── CreatorStudio.vue    # Creator Dashboard
│   │   │   ├── MembershipsView.vue  # [MVP 2] Creator Subscriptions & Paywall Manager
│   │   │   ├── TaxReportsView.vue   # [MVP 2] Financial Ledger & Tax Estimator
│   │   │   ├── CheckoutView.vue     # Payment & QRIS Modal
│   │   │   └── SecureReaderView.vue # Protected Content Viewer
│   │   └── stores/
│
├── backend/                         # Java Spring Boot 3 + Lombok
│   └── src/main/java/com/platform/
│       ├── config/                  # SecurityConfig, RedisConfig, CorsConfig
│       ├── controller/
│       │   ├── AuthController.java
│       │   ├── BioLinkController.java
│       │   ├── ProductController.java
│       │   ├── OrderController.java
│       │   ├── PaymentWebhookController.java
│       │   ├── MembershipController.java      # [MVP 2]
│       │   ├── TaxAccountingController.java   # [MVP 2]
│       │   └── GamificationController.java    # [MVP 2]
│       ├── dto/                     # Request & Response DTOs with Lombok
│       ├── entity/                  # JPA Entities (Lombok @Getter/@Setter)
│       ├── repository/              # Spring Data JPA Repositories
│       └── service/                 # Business logic, Payout, Redis publisher
│
└── worker/                          # Python Security & Media Worker
    └── app/
        ├── config.py                # SQLModel DB & Redis config
        ├── models.py                # SQLModel entity tables
        ├── main.py                  # Redis worker / FastAPI task listener
        └── services/
            ├── watermarker.py       # [MVP 1] PyMuPDF dynamic stamping
            ├── video_drm.py         # [MVP 2] FFmpeg HLS chunking & AES-128
            └── tax_exporter.py      # [MVP 2] Tax report & CSV/PDF generator
```

---

## Detailed Specifications: MVP 1 vs. MVP 2

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ROADMAP SPECIFICATIONS                            │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ 🚀 MVP 1 (Launch Core + Anti-Piracy) │ 💎 MVP 2 (Ecosystem & Moat Expansion)│
├──────────────────────────────────────┼──────────────────────────────────────┤
│ 1. SaaS Platform Landing Page        │ 1. 12-Column Visual Grid Builder     │
│ 2. Creator Bio-Link & Storefront     │ 2. Interactive In-Bio Widgets        │
│ 3. Digital Storefront (E-books/PDF)  │ 3. Tiered Memberships & Paywalls     │
│ 4. Payment Gateway (QRIS, VA)        │ 4. Video DRM & HLS Streaming Engine  │
│ 5. Dynamic PDF Watermarking (Python) │ 5. Creator Tax (PPh) & Accounting    │
│ 6. In-App Canvas Reader (Anti-Copy)  │ 6. Buyer Reward Points & Flash Sales │
│ 7. Basic Sales Dashboard & Payout    │                                      │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

---

### Phase 1: MVP 1 Detailed Implementation

#### 1.1 Backend Core (Spring Boot + Lombok)
- **Entities**:
  - `User` (id, email, password, role: CREATOR / BUYER / ADMIN).
  - `CreatorProfile` (id, userId, username, displayName, bio, avatarUrl, themeConfigJson).
  - `BioBlock` (id, profileId, title, url, blockType: LINK / PRODUCT / EMBED, sortOrder).
  - `DigitalProduct` (id, creatorId, title, price, masterFileUrl, enableWatermark, isPublished).
  - `Order` (id, productId, buyerEmail, buyerPhone, totalAmount, status: PENDING/PAID/FAILED, paymentRef, watermarkedFileUrl).
- **Payment & Task Dispatcher**:
  - Midtrans/Xendit QRIS webhook handler.
  - Upon `status == PAID`: push event to Redis queue:
    `{"event": "WATERMARK_PDF", "order_id": "...", "buyer_email": "...", "buyer_phone": "..."}`.

#### 1.2 Python Security Worker (PyMuPDF + SQLModel)
- Subscribes to Redis `order_tasks` queue.
- Fetches master PDF from private bucket.
- Stamps buyer identification on diagonal canvas and footer across every page using PyMuPDF.
- Uploads watermarked PDF and updates database record (`watermarked_file_url`).

#### 1.3 Vue 3 Frontend (TailwindCSS)
- **Platform Landing Page (`/`)**: Hero banner, anti-piracy feature showcase, comparison table, CTA.
- **Creator Studio (`/studio`)**: Split layout with Live Mobile Mockup and instant link/product editor.
- **Public Bio Storefront (`/@username`)**: Ultra-responsive mobile bio page.
- **Secure Reader (`/read/:orderId`)**: HTML5 Canvas rendering of the PDF with disabled right-click, print blocker (`Ctrl+P`), and watermark overlays.

---

### Phase 2: MVP 2 Detailed Implementation

#### 2.1 12-Column Drag-and-Drop Grid Builder & Widgets (Vue 3 + Spring Boot)
- **Visual Grid Canvas**:
  - Replace vertical lists with a customizable 12-column responsive layout (`VueDraggable` / `GridStack`).
  - Creators can resize blocks (1x1 square cards, 2x1 wide banners, 3x2 featured boxes).
- **Interactive Widgets**:
  - **Mini Financial Calculator**: Creators set variables (e.g. "Calculate your freelance rate") and viewers get instant results without leaving the bio page.
  - **Lead Capture & Newsletter Box**: Collects buyer emails directly synced to Mailchimp/Brevo.
  - **Interactive Polls**: Live audience voting with instant percentage results.

#### 2.2 Tiered Memberships & In-Bio Paywall (Spring Boot + Vue 3)
- **Membership Engine**:
  - Creators configure tiers (e.g., *Silver Rp 50.000/mo*, *Gold Rp 150.000/mo*).
  - Recurring invoice/subscription billing integration via Payment Gateway.
  - Members-only feed on the creator's page (exclusive articles, audio notes, files).
- **Micro-Paywall Block**:
  - Single locked content blocks directly in the bio link (e.g., "Unlock this case study for Rp 10.000").
  - Instant modal checkout unlocking the inline text/video immediately.

#### 2.3 Video DRM & Encrypted Streaming (Python Worker + Vue 3)
- **Worker Transcoding Pipeline**:
  - Python worker invokes `FFmpeg` to transcode master MP4 video into **HLS (`.m3u8`) segments**.
  - Encrypts video segments using **AES-128 encryption keys**.
- **Player Access Control**:
  - Spring Boot generates short-lived one-time tokens for video keys.
  - In-app Video.js player decrypts and streams video in memory; raw video download is impossible.

#### 2.4 Automated Creator Accounting & Tax Engine (Spring Boot)
- **Financial Ledger**:
  - Real-time calculations: `Gross Revenue` - `Gateway Fees` - `Platform Take Rate` - `Affiliate Commissions` = `Net Profit`.
- **Indonesian Tax (PPh) Estimation**:
  - Calculates estimated **PPh Final (PP 55/2022 - 0.5% for UMKM)** or **PPh 21** based on creator tax status.
  - Generates downloadable quarterly/annual tax CSV and PDF reports ready for Indonesian annual tax return (SPT Tahunan).

#### 2.5 Gamification & Buyer Loyalty Engine
- **Reward Points**:
  - 1 Point per Rp 1.000 spent. Points can be redeemed for discounts on subsequent purchases from that specific creator.
- **Smart Bundling & Flash Sales**:
  - Dynamic countdown timer banner: *"Flash Sale ends in 01:45:20"*.
  - Bundle discount engine: Auto-applies 25% discount when Buyer adds both E-Book A and Template B to cart.

---

## Verification & Testing Plan

### Automated Tests:
1. **Spring Boot (JUnit 5 + Mockito)**:
   - `OrderServiceTest`: Verify transaction atomicity on checkout and balance updates.
   - `TaxAccountingTest`: Validate Indonesian PPh 21 and PPh Final calculation formulas.
   - `MembershipAccessTest`: Verify locked content returns 403 Forbidden for non-subscribers.
2. **Python Worker (PyTest)**:
   - `test_pdf_watermark`: Assert stamped output contains buyer email and order ID.
   - `test_video_hls_drm`: Verify `.m3u8` playlist contains `#EXT-X-KEY:METHOD=AES-128`.

### Manual & Security Verification:
1. **DRM Security Audit**: Verify network inspecting tools cannot extract raw MP4 or un-watermarked PDF source files.
2. **End-to-End Subscription Test**: Subscribe to a test creator tier and verify locked feed blocks unlock instantly upon webhook receipt.
