# 🚀 Backend Engineering Mastery — Project Roadmap

> **Project: SmartHire Platform** — Sebuah platform job-matching berbasis AI yang menghubungkan kandidat dengan perusahaan, dengan fitur semantic search menggunakan Vector DB, concurrent booking interview, dan full infra deployment.
>
> Kenapa project ini? Karena **satu project ini cukup kompleks** untuk menyentuh semua topik yang kamu butuhkan: TDD, System Design, Idempotency, Race Condition, Database Design, REST API, Docker/K8s, dan Vector DB — tanpa terasa dipaksakan.

---

## 🗺️ Overview Milestone

| # | Milestone                     | Fokus Utama                                     | Estimasi    |
| - | ----------------------------- | ----------------------------------------------- | ----------- |
| 1 | Foundation & TDD Setup        | JUnit, Mockito, TDD workflow                    | 1–2 minggu |
| 2 | Core Domain & Database Design | Data modeling, normalization, relationships     | 1–2 minggu |
| 3 | REST API Development          | REST best practices, validation, error handling | 1–2 minggu |
| 4 | Concurrency & Idempotency     | Race condition, idempotent APIs                 | 1 minggu    |
| 5 | System Design & Architecture  | High-level design, scalability thinking         | 1 minggu    |
| 6 | Vector DB & AI Features       | Semantic search, embeddings                     | 1 minggu    |
| 7 | Docker & Kubernetes           | Containerization, orchestration                 | 1–2 minggu |
| 8 | Infra & Observability         | Infra basics, monitoring, CI/CD                 | 1 minggu    |

---

## 📦 Tech Stack

```
Backend  : Java (Spring Boot 3.x)
Test     : JUnit 5 + Mockito
Database : PostgreSQL (relational) + Qdrant (Vector DB)
ORM      : JPA / Hibernate
Cache    : Redis
Queue    : (opsional) Kafka atau RabbitMQ
Container: Docker + Kubernetes (minikube atau k3s lokal)
CI/CD    : GitHub Actions
Infra    : Docker Compose → Kubernetes manifests
```

---

## 🎯 Milestone 1 — Foundation & TDD Setup

> **Goal**: Project berjalan, test infrastructure siap, kamu terbiasa dengan Red → Green → Refactor cycle.

### Kenapa ini dulu?

Interview TDD bukan cuma soal nulis test — mereka mau lihat kamu *think in tests*. Kalau dari awal kamu build dengan TDD mindset, semua milestone berikutnya otomatis punya test coverage.

---

### Task 1.1 — Project Scaffolding

- [X] Init Spring Boot project via [start.spring.io](https://start.spring.io) dengan dependencies: `Spring Web`, `Spring Data JPA`, `Spring Security`, `Validation`, `Lombok`, `Testcontainers`
- [X] Setup struktur package:
  ```
  com.interview.astrapay
  ├── domain/         ← entity + value objects
  ├── application/    ← service layer (business logic)
  ├── infrastructure/ ← repository, external integrations
  ├── presentation/   ← controller, DTO
  └── config/         ← Spring config
  ```
- [X] Tambahkan dependency: `junit-jupiter`, `mockito-core`, `mockito-junit-jupiter` di `pom.xml`
- [X] Setup `application.yml` untuk profile `dev`, `test`, `prod`
- [X] Pastikan `./mvnw test` jalan tanpa error

---

### Task 1.2 — Menulis Test Pertama dengan TDD (Red → Green → Refactor)

**Contoh domain yang di-TDD**: `CandidateRegistrationService`

**Step-by-step TDD workflow:**

```
RED   → Tulis test yang gagal dulu
GREEN → Tulis kode minimal agar test pass
REFACTOR → Bersihkan tanpa mengubah behaviour
```

- [ ] **RED**: Tulis test untuk `registerCandidate()`:
  ```java
  @Test
  void shouldRegisterCandidateSuccessfully() {
      // given
      CandidateRegistrationRequest request = new CandidateRegistrationRequest("john@email.com", "John Doe");

      // when
      CandidateRegistrationResult result = candidateService.register(request);

      // then
      assertThat(result.candidateId()).isNotNull();
      assertThat(result.email()).isEqualTo("john@email.com");
  }
  ```
- [ ] Pastikan test **merah** dulu sebelum nulis implementasi
- [ ] **GREEN**: Implementasi `CandidateRegistrationService` minimal
- [ ] **REFACTOR**: Extract method, rename variable supaya readable
- [ ] Ulangi siklus ini untuk semua service yang dibuat di milestone berikutnya

---

### Task 1.3 — Unit Test dengan Mockito

- [ ] Pahami perbedaan `@Mock` vs `@InjectMocks` vs `@Spy`
- [ ] Test service yang punya dependency ke repository:
  ```java
  @ExtendWith(MockitoExtension.class)
  class CandidateServiceTest {

      @Mock
      private CandidateRepository candidateRepository;

      @InjectMocks
      private CandidateService candidateService;

      @Test
      void shouldThrowExceptionWhenEmailAlreadyExists() {
          // given
          when(candidateRepository.existsByEmail("john@email.com")).thenReturn(true);

          // when & then
          assertThatThrownBy(() -> candidateService.register(request))
              .isInstanceOf(DuplicateEmailException.class);

          verify(candidateRepository, never()).save(any());
      }
  }
  ```
- [ ] Pelajari `verify()`, `when().thenReturn()`, `doThrow()`, `ArgumentCaptor`
- [ ] Tulis test untuk edge case: null input, duplicate email, invalid format

---

### Task 1.4 — Integration Test dengan Testcontainers

- [ ] Setup Testcontainers untuk PostgreSQL:
  ```java
  @SpringBootTest
  @Testcontainers
  class CandidateRepositoryIntegrationTest {

      @Container
      static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

      @DynamicPropertySource
      static void configureProperties(DynamicPropertyRegistry registry) {
          registry.add("spring.datasource.url", postgres::getJdbcUrl);
      }
  }
  ```
- [ ] Tulis integration test untuk repository layer
- [ ] Pastikan test tidak saling bergantung (isolated, idempotent tests)

---

### ✅ Checkpoint Milestone 1

Kamu sudah bisa menjawab di interview:

- *"Jelaskan workflow TDD kamu"* → Red-Green-Refactor
- *"Apa bedanya unit test vs integration test?"*
- *"Kapan pakai @Mock vs @Spy?"*
- *"Gimana kamu test sesuatu yang punya external dependency?"* → Mockito

---

## 🗄️ Milestone 2 — Core Domain & Database Design

> **Goal**: ERD yang solid, normalization yang tepat, dan data model yang mencerminkan domain bisnis.

---

### Task 2.1 — Domain Analysis & Data Modeling

Identifikasi entitas utama SmartHire:

```
Entitas Utama:
- Candidate      (job seeker)
- Company        (employer)
- JobPosting     (lowongan)
- Application    (lamaran)
- InterviewSlot  (jadwal interview)
- Skill          (keahlian)
- Resume         (CV kandidat)
```

- [ ] Buat **Entity Relationship Diagram (ERD)** dengan tools: dbdiagram.io atau draw.io
- [ ] Identifikasi:
  - **1:1** → Candidate ↔ Resume
  - **1:N** → Company → JobPosting, JobPosting → Application
  - **M:N** → Candidate ↔ Skill (via junction table `candidate_skills`)
- [ ] Definisikan **aggregate roots** (DDD mindset): `Candidate`, `JobPosting`, `Application`

---

### Task 2.2 — Database Normalization

Latihan normalization dengan tabel contoh:

- [ ] **1NF**: Pastikan tidak ada repeating group. Contoh: jangan simpan `skills` sebagai `"Java,Python,Go"` — pisah ke tabel sendiri
- [ ] **2NF**: Hilangkan partial dependency. Contoh: kalau `application_skills` punya kolom `skill_name`, padahal `skill_name` hanya bergantung pada `skill_id` → pindahkan ke tabel `skills`
- [ ] **3NF**: Hilangkan transitive dependency. Contoh: `city` dan `province` di tabel `candidate` — `province` bergantung pada `city`, bukan `candidate_id` → pisah ke tabel `locations`

```sql
-- SEBELUM (tidak normalized)
CREATE TABLE applications (
    id UUID,
    candidate_name VARCHAR,  -- transitive: name bergantung pada candidate_id
    company_name VARCHAR,    -- transitive: company_name bergantung pada company_id
    skills TEXT              -- 1NF violation: comma-separated
);

-- SESUDAH (3NF)
CREATE TABLE applications (
    id UUID PRIMARY KEY,
    candidate_id UUID REFERENCES candidates(id),
    job_posting_id UUID REFERENCES job_postings(id),
    applied_at TIMESTAMP,
    status VARCHAR(20)
);
```

---

### Task 2.3 — Denormalization untuk Read Performance

- [ ] Identifikasi query yang sering dipanggil: *"Tampilkan list aplikasi beserta nama kandidat dan nama perusahaan"*
- [ ] Buat **materialized view** atau **denormalized read table** untuk query tersebut
- [ ] Dokumentasikan **trade-off**: write complexity naik, tapi read speed meningkat
- [ ] Tambahkan index yang tepat:
  ```sql
  CREATE INDEX idx_applications_candidate_id ON applications(candidate_id);
  CREATE INDEX idx_applications_status ON applications(status);
  CREATE INDEX idx_job_postings_company_id_status ON job_postings(company_id, status);
  ```

---

### Task 2.4 — Schema Migration dengan Flyway/Liquibase

- [ ] Setup Flyway di Spring Boot
- [ ] Buat migration scripts:
  ```
  V1__create_candidates_table.sql
  V2__create_companies_table.sql
  V3__create_job_postings_table.sql
  V4__create_applications_table.sql
  V5__create_skills_and_junction_tables.sql
  V6__add_indexes.sql
  ```
- [ ] Test rollback scenario: apa yang terjadi kalau migration V4 gagal?

---

### ✅ Checkpoint Milestone 2

Kamu sudah bisa menjawab di interview:

- *"Jelaskan normalization, sampai 3NF"* → bisa kasih contoh konkret dari project
- *"Kapan kamu denormalize?"* → bisa jawab dengan trade-off
- *"Gimana cara kamu model M:N relationship?"* → junction table dengan proper FK
- *"Bedanya ERD dan data model?"*

---

## 🌐 Milestone 3 — REST API Development

> **Goal**: API yang clean, versioned, well-documented, dan mengikuti REST conventions.

---

### Task 3.1 — REST API Design

Design endpoint sebelum implementasi (API-first approach):

```
# Candidate
POST   /api/v1/candidates              → Register kandidat
GET    /api/v1/candidates/{id}         → Get profil kandidat
PUT    /api/v1/candidates/{id}         → Update profil
PATCH  /api/v1/candidates/{id}/skills  → Update skills

# Job Postings
POST   /api/v1/job-postings            → Buat lowongan (Company)
GET    /api/v1/job-postings            → List lowongan (dengan filter, pagination)
GET    /api/v1/job-postings/{id}       → Detail lowongan
DELETE /api/v1/job-postings/{id}       → Tutup lowongan (soft delete)

# Applications
POST   /api/v1/job-postings/{id}/apply → Lamar pekerjaan
GET    /api/v1/candidates/{id}/applications → Riwayat lamaran
PATCH  /api/v1/applications/{id}/status    → Update status (Company)

# Interview
POST   /api/v1/interviews/slots        → Buat slot interview
POST   /api/v1/interviews/book         → Book slot interview (⚠️ race condition area)
```

- [ ] Dokumentasikan setiap endpoint dengan request/response schema
- [ ] Tentukan HTTP status code yang tepat: `200`, `201`, `400`, `404`, `409`, `422`, `500`
- [ ] Design konsisten error response:
  ```json
  {
    "timestamp": "2024-01-15T10:30:00Z",
    "status": 400,
    "error": "VALIDATION_FAILED",
    "message": "Email format is invalid",
    "path": "/api/v1/candidates",
    "traceId": "abc123xyz"
  }
  ```

---

### Task 3.2 — Implementasi Controller + Validation (TDD)

- [ ] Tulis test controller dulu dengan `@WebMvcTest` + MockMvc:
  ```java
  @WebMvcTest(CandidateController.class)
  class CandidateControllerTest {

      @Autowired
      private MockMvc mockMvc;

      @MockBean
      private CandidateService candidateService;

      @Test
      void shouldReturn201WhenCandidateRegistered() throws Exception {
          mockMvc.perform(post("/api/v1/candidates")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                      {"email": "john@email.com", "name": "John Doe"}
                      """))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.candidateId").isNotEmpty());
      }

      @Test
      void shouldReturn400WhenEmailIsInvalid() throws Exception {
          mockMvc.perform(post("/api/v1/candidates")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""{"email": "not-an-email", "name": "John"}"""))
              .andExpect(status().isBadRequest());
      }
  }
  ```
- [ ] Implementasi DTO dengan Bean Validation: `@NotNull`, `@Email`, `@Size`, custom `@ValidSkillLevel`
- [ ] Buat `GlobalExceptionHandler` dengan `@ControllerAdvice`
- [ ] Implementasi pagination: `GET /job-postings?page=0&size=20&sort=createdAt,desc`

---

### Task 3.3 — API Documentation dengan OpenAPI/Swagger

- [ ] Tambahkan `springdoc-openapi-starter-webmvc-ui`
- [ ] Anotasi controller dengan `@Operation`, `@ApiResponse`, `@Schema`
- [ ] Pastikan Swagger UI dapat diakses di `http://localhost:8080/swagger-ui.html`

---

### ✅ Checkpoint Milestone 3

Kamu sudah bisa menjawab di interview:

- *"Apa bedanya PUT vs PATCH?"*
- *"Kapan pakai 400 vs 422?"*
- *"Gimana kamu design versioning API?"*
- *"Jelaskan stateless di REST"*

---

## ⚡ Milestone 4 — Concurrency & Idempotency ⭐ (Special Case)

> **Goal**: Ini adalah milestone yang paling sering jadi bahan interview. Kuasai ini dengan baik.

---

### Task 4.1 — Memahami Race Condition

**Scenario**: Dua kandidat mencoba book **slot interview yang sama** secara bersamaan.

```
Timeline tanpa proteksi:
T1: Kandidat A cek slot → status: AVAILABLE ✓
T2: Kandidat B cek slot → status: AVAILABLE ✓
T3: Kandidat A book → UPDATE status = BOOKED
T4: Kandidat B book → UPDATE status = BOOKED  ← ⚠️ Double booking!
```

- [ ] **Reproduksi masalahnya** dulu. Tulis test yang membuktikan race condition terjadi:
  ```java
  @Test
  void shouldNotAllowDoubleBookingUnderConcurrentRequests() throws InterruptedException {
      InterviewSlot slot = createAvailableSlot();

      ExecutorService executor = Executors.newFixedThreadPool(2);
      AtomicInteger successCount = new AtomicInteger(0);
      AtomicInteger failCount = new AtomicInteger(0);

      CountDownLatch latch = new CountDownLatch(2);

      for (int i = 0; i < 2; i++) {
          executor.submit(() -> {
              try {
                  interviewService.bookSlot(slot.getId(), candidateId);
                  successCount.incrementAndGet();
              } catch (SlotAlreadyBookedException e) {
                  failCount.incrementAndGet();
              } finally {
                  latch.countDown();
              }
          });
      }

      latch.await(5, TimeUnit.SECONDS);
      assertThat(successCount.get()).isEqualTo(1);
      assertThat(failCount.get()).isEqualTo(1);
  }
  ```

---

### Task 4.2 — Solusi Race Condition

Implementasikan 3 solusi dan pahami trade-off masing-masing:

**Solusi 1: Optimistic Locking (JPA `@Version`)**

```java
@Entity
public class InterviewSlot {
    @Version
    private Long version;
  
    // Kalau ada conflict → throws OptimisticLockException
    // Cocok untuk: low contention, lebih banyak read daripada write
}
```

**Solusi 2: Pessimistic Locking (`SELECT FOR UPDATE`)**

```java
@Repository
interface InterviewSlotRepository extends JpaRepository<InterviewSlot, UUID> {
  
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InterviewSlot s WHERE s.id = :id")
    Optional<InterviewSlot> findByIdForUpdate(@Param("id") UUID id);
  
    // Cocok untuk: high contention, write-heavy scenario
    // Trade-off: bisa terjadi deadlock kalau tidak hati-hati
}
```

**Solusi 3: Database Unique Constraint**

```sql
ALTER TABLE interview_bookings 
ADD CONSTRAINT uq_slot_booking UNIQUE (slot_id);
-- DB-level guarantee, paling aman, tapi perlu handle exception
```

- [ ] Implementasikan Solusi 1 (Optimistic Locking) untuk interview booking
- [ ] Implementasikan Solusi 2 (Pessimistic Locking) sebagai alternatif
- [ ] Dokumentasikan kapan memilih masing-masing solusi
- [ ] Test concurrent booking dengan JUnit 5 + `ExecutorService`

---

### Task 4.3 — Idempotency

**Scenario**: Kandidat klik tombol "Lamar" dua kali karena koneksi lambat. Jangan sampai ada duplikat application.

**Konsep**: Sebuah operasi disebut idempotent kalau dipanggil berkali-kali menghasilkan efek yang sama seperti dipanggil sekali.

```
HTTP Methods yang naturally idempotent: GET, PUT, DELETE
HTTP Methods yang TIDAK idempotent by default: POST ← yang perlu kita handle
```

- [ ] Implementasikan **Idempotency Key** di header:

  ```
  POST /api/v1/job-postings/{id}/apply
  Headers:
    Idempotency-Key: client-generated-uuid-12345
  ```
- [ ] Buat `IdempotencyService`:

  ```java
  @Service
  public class IdempotencyService {

      private final RedisTemplate<String, IdempotencyRecord> redisTemplate;

      public Optional<IdempotencyRecord> findExistingResult(String idempotencyKey) {
          return Optional.ofNullable(
              redisTemplate.opsForValue().get("idempotency:" + idempotencyKey)
          );
      }

      public void saveResult(String idempotencyKey, IdempotencyRecord record) {
          redisTemplate.opsForValue().set(
              "idempotency:" + idempotencyKey,
              record,
              24, TimeUnit.HOURS  // TTL 24 jam
          );
      }
  }
  ```
- [ ] Buat `@IdempotentOperation` annotation + AOP interceptor
- [ ] Test scenario:

  - Request pertama → `201 Created` + simpan ke Redis
  - Request kedua dengan key yang sama → `200 OK` + return cached result (tanpa DB write baru)
  - Request dengan key baru → proses normal

---

### ✅ Checkpoint Milestone 4

Kamu sudah bisa menjawab di interview:

- *"Jelaskan race condition dan bagaimana cara mencegahnya"*
- *"Apa bedanya optimistic vs pessimistic locking? Kapan pakai yang mana?"*
- *"Apa itu idempotency? Kasih contoh real case"*
- *"Gimana kamu implement idempotency di POST endpoint?"*
- *"Apa yang terjadi kalau user double-submit form?"*

---

## 🏗️ Milestone 5 — System Design & Architecture

> **Goal**: Bisa menjelaskan arsitektur project dari high-level, termasuk skalabilitas dan bottleneck.

---

### Task 5.1 — High-Level System Design

Gambar (bisa di paper atau Excalidraw) arsitektur SmartHire:

```
Client (Web/Mobile)
       │
       ▼
  [API Gateway / Load Balancer]
       │
  ┌────┴─────┐
  │          │
  ▼          ▼
[Auth     [SmartHire
Service]   API Server]
               │
     ┌─────────┼──────────┐
     ▼         ▼          ▼
[PostgreSQL] [Redis]  [Qdrant
  (Primary    (Cache   Vector DB]
  + Replica)  + Idempotency)
```

- [ ] Identifikasi **bottleneck** di setiap layer
- [ ] Dokumentasikan keputusan arsitektur (ADR — Architecture Decision Record):
  ```markdown
  ## ADR-001: Pilih PostgreSQL bukan MongoDB

  **Status**: Accepted
  **Context**: Data kandidat dan lamaran punya relasi yang kompleks
  **Decision**: PostgreSQL karena ACID compliance dan relational queries yang efisien
  **Consequences**: Schema migration lebih rigid, tapi data integrity terjamin
  ```
- [ ] Pikirkan: *"Gimana kalau concurrent user naik 10x?"*

---

### Task 5.2 — Caching Strategy

- [ ] Identifikasi data yang layak di-cache: job postings list, company profiles (baca sering, tulis jarang)
- [ ] Implementasikan `@Cacheable` dengan Spring Cache + Redis:
  ```java
  @Cacheable(value = "job-postings", key = "#companyId + '-' + #page")
  public Page<JobPostingDTO> getJobPostings(UUID companyId, int page) { ... }

  @CacheEvict(value = "job-postings", allEntries = true)
  public void createJobPosting(CreateJobPostingRequest request) { ... }
  ```
- [ ] Pahami **Cache invalidation**: kapan cache harus di-evict?
- [ ] Dokumentasikan **cache-aside** vs **write-through** strategy

---

### Task 5.3 — API Rate Limiting

- [ ] Implementasikan rate limiting dengan Bucket4j atau Redis:
  ```
  Rule: Max 100 requests per minute per IP untuk endpoint publik
  Rule: Max 10 POST /apply per hari per kandidat
  ```
- [ ] Return `429 Too Many Requests` dengan header `Retry-After`

---

### ✅ Checkpoint Milestone 5

Kamu sudah bisa menjawab di interview:

- *"Gimana kamu design sistem yang bisa handle 1 juta user?"*
- *"Di mana bottleneck paling likely di sistem kamu?"*
- *"Jelaskan caching strategy kamu"*
- *"Apa itu CAP theorem?"* → bisa connect ke pilihan PostgreSQL vs NoSQL

---

## 🧠 Milestone 6 — Vector DB & AI Features ⭐ (Special Case)

> **Goal**: Semantic job-matching menggunakan embeddings — ini yang bikin SmartHire beda dari job board biasa.

---

### Task 6.1 — Konsep Dasar Vector DB

- [ ] Pahami perbedaan:
  ```
  Traditional DB: "SELECT * WHERE skill = 'Java'"
      → Exact match

  Vector DB: "Find jobs similar to candidate's profile"
      → Semantic similarity (cosine distance)
  ```
- [ ] Setup Qdrant dengan Docker:
  ```yaml
  # docker-compose.yml
  qdrant:
    image: qdrant/qdrant:latest
    ports:
      - "6333:6333"
    volumes:
      - qdrant_data:/qdrant/storage
  ```
- [ ] Pahami konsep: **embedding**, **vector**, **cosine similarity**, **collection**

---

### Task 6.2 — Semantic Job Matching

- [ ] Pilih embedding model: OpenAI `text-embedding-3-small` atau lokal via `Ollama`
- [ ] Buat `EmbeddingService`:
  ```java
  @Service
  public class EmbeddingService {

      public float[] generateEmbedding(String text) {
          // Call embedding API
          // Return 1536-dimensional vector (OpenAI) atau 768 (BERT)
      }
  }
  ```
- [ ] Indexing job postings ke Qdrant:
  ```java
  // Saat job posting dibuat → generate embedding dari title + description + skills
  // Store di Qdrant dengan payload: { jobPostingId, companyId, salaryRange }

  qdrantClient.upsert("job-postings", List.of(
      new PointStruct(
          jobPosting.getId().toString(),
          new VectorData(embedding),
          Map.of("jobPostingId", jobPosting.getId(), 
                 "companyId", jobPosting.getCompanyId())
      )
  ));
  ```
- [ ] Search kandidat yang cocok:
  ```java
  public List<JobRecommendation> findMatchingJobs(UUID candidateId) {
      Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
      String candidateProfile = buildProfileText(candidate);
      float[] queryVector = embeddingService.generateEmbedding(candidateProfile);

      return qdrantClient.search("job-postings", queryVector, 10)
          .stream()
          .map(hit -> new JobRecommendation(
              (UUID) hit.getPayload().get("jobPostingId"),
              hit.getScore()
          ))
          .toList();
  }
  ```
- [ ] Buat endpoint: `GET /api/v1/candidates/{id}/recommended-jobs`
- [ ] Test kualitas recommendation dengan beberapa test case manual

---

### Task 6.3 — Hybrid Search (Vector + Filter)

- [ ] Kombinasikan semantic search dengan filter:
  ```java
  // "Cari jobs yang mirip profile kamu DAN salary >= 10jt DAN location = Jakarta"
  SearchRequest request = SearchRequest.builder()
      .vector(queryVector)
      .filter(Filter.must(
          Condition.match("location", "Jakarta"),
          Condition.range("salaryMin", Range.gte(10_000_000))
      ))
      .limit(10)
      .build();
  ```

---

### ✅ Checkpoint Milestone 6

Kamu sudah bisa menjawab di interview:

- *"Apa itu Vector DB dan bedanya sama traditional DB?"*
- *"Gimana cara kerja semantic search?"*
- *"Apa itu embedding?"*
- *"Gimana kamu integrate AI search ke existing sistem?"*

---

## 🐳 Milestone 7 — Docker & Kubernetes ⭐ (Special Case)

> **Goal**: Aplikasi berjalan di container dan bisa di-orchestrate dengan Kubernetes.

---

### Task 7.1 — Dockerize Aplikasi

- [ ] Buat `Dockerfile` yang production-grade:
  ```dockerfile
  # Multi-stage build
  FROM eclipse-temurin:21-jdk AS builder
  WORKDIR /app
  COPY pom.xml .
  COPY src ./src
  RUN mvn clean package -DskipTests

  FROM eclipse-temurin:21-jre AS runtime
  WORKDIR /app

  # Security: non-root user
  RUN useradd -m -u 1001 appuser
  USER appuser

  COPY --from=builder /app/target/*.jar app.jar

  EXPOSE 8080
  HEALTHCHECK --interval=30s --timeout=3s \
      CMD wget -qO- http://localhost:8080/actuator/health || exit 1

  ENTRYPOINT ["java", "-jar", "app.jar"]
  ```
- [ ] Buat `.dockerignore` yang tepat
- [ ] Build image: `docker build -t smarthire-api:1.0.0 .`

---

### Task 7.2 — Docker Compose untuk Local Development

- [ ] Buat `docker-compose.yml` lengkap:
  ```yaml
  version: '3.8'
  services:
    app:
      build: .
      ports:
        - "8080:8080"
      environment:
        SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/smarthire
        SPRING_REDIS_HOST: redis
      depends_on:
        postgres:
          condition: service_healthy
        redis:
          condition: service_healthy

    postgres:
      image: postgres:15
      environment:
        POSTGRES_DB: smarthire
        POSTGRES_PASSWORD: secret
      healthcheck:
        test: ["CMD-SHELL", "pg_isready -U postgres"]
        interval: 10s
      volumes:
        - postgres_data:/var/lib/postgresql/data

    redis:
      image: redis:7-alpine
      healthcheck:
        test: ["CMD", "redis-cli", "ping"]

    qdrant:
      image: qdrant/qdrant:latest
      ports:
        - "6333:6333"
  ```
- [ ] Jalankan: `docker compose up -d`
- [ ] Test endpoint berjalan di dalam container

---

### Task 7.3 — Kubernetes Deployment (Minikube)

- [ ] Install Minikube: `minikube start`
- [ ] Buat Kubernetes manifests di folder `k8s/`:

  ```yaml
  # k8s/deployment.yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: smarthire-api
  spec:
    replicas: 2
    selector:
      matchLabels:
        app: smarthire-api
    template:
      metadata:
        labels:
          app: smarthire-api
      spec:
        containers:
        - name: api
          image: smarthire-api:1.0.0
          ports:
          - containerPort: 8080
          env:
          - name: SPRING_DATASOURCE_PASSWORD
            valueFrom:
              secretKeyRef:
                name: smarthire-secrets
                key: db-password
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
  ```

  ```yaml
  # k8s/service.yaml
  apiVersion: v1
  kind: Service
  metadata:
    name: smarthire-api-svc
  spec:
    selector:
      app: smarthire-api
    ports:
    - port: 80
      targetPort: 8080
    type: LoadBalancer
  ```

  ```yaml
  # k8s/configmap.yaml + secret.yaml
  # Store config terpisah dari image
  ```
- [ ] Deploy: `kubectl apply -f k8s/`
- [ ] Test: `kubectl get pods`, `kubectl logs`, `kubectl describe`
- [ ] Coba rolling update: ubah replicas jadi 3, apply, lihat proses
- [ ] Coba rollback: `kubectl rollout undo deployment/smarthire-api`

---

### ✅ Checkpoint Milestone 7

Kamu sudah bisa menjawab di interview:

- *"Apa bedanya Docker vs Kubernetes?"*
- *"Jelaskan multi-stage build dan kenapa itu penting"*
- *"Gimana K8s handle service discovery?"*
- *"Apa itu liveness vs readiness probe?"*
- *"Gimana cara rollback deployment di K8s?"*

---

## 🏗️ Milestone 8 — Infra Basic & Observability ⭐ (Special Case)

> **Goal**: Kamu bisa explain infra di balik aplikasimu — bukan cuma nulis kode.

---

### Task 8.1 — CI/CD dengan GitHub Actions

- [ ] Buat `.github/workflows/ci.yml`:
  ```yaml
  name: CI Pipeline

  on:
    push:
      branches: [main, develop]
    pull_request:
      branches: [main]

  jobs:
    test:
      runs-on: ubuntu-latest
      services:
        postgres:
          image: postgres:15
          env:
            POSTGRES_PASSWORD: test
          options: >-
            --health-cmd pg_isready
            --health-interval 10s

      steps:
        - uses: actions/checkout@v4
        - uses: actions/setup-java@v4
          with:
            java-version: '21'
        - name: Run tests
          run: ./mvnw test
        - name: Upload coverage to Codecov
          uses: codecov/codecov-action@v3

    build:
      needs: test
      runs-on: ubuntu-latest
      steps:
        - name: Build Docker image
          run: docker build -t smarthire-api:${{ github.sha }} .
        - name: Push to registry
          run: docker push ...
  ```
- [ ] Setup environment secrets di GitHub repo settings

---

### Task 8.2 — Observability Stack

- [ ] Tambahkan Spring Actuator endpoints: `/health`, `/metrics`, `/info`
- [ ] Setup structured logging dengan correlation ID:
  ```java
  // Setiap request punya traceId yang sama di semua log
  @Component
  public class RequestLoggingFilter implements Filter {
      public void doFilter(request, response, chain) {
          String traceId = UUID.randomUUID().toString().substring(0, 8);
          MDC.put("traceId", traceId);
          response.addHeader("X-Trace-Id", traceId);
          chain.doFilter(request, response);
          MDC.clear();
      }
  }
  ```
- [ ] Setup Prometheus + Grafana dengan Docker Compose untuk monitoring
- [ ] Buat custom metric:
  ```java
  @Autowired
  private MeterRegistry meterRegistry;

  // Di service layer
  meterRegistry.counter("application.submitted", "company", companyId.toString()).increment();
  ```
- [ ] Buat Grafana dashboard sederhana: request rate, error rate, latency (RED method)

---

### Task 8.3 — Environment & Secret Management

- [ ] Jangan pernah hardcode credential di kode
- [ ] Gunakan environment variables + Spring `@ConfigurationProperties`
- [ ] Kubernetes Secrets untuk production
- [ ] Dokumentasikan semua environment variables yang dibutuhkan di `README.md`

---

### Task 8.4 — README yang Baik

- [ ] Buat `README.md` yang mencakup:
  - Architecture diagram
  - Prerequisites
  - How to run locally (`docker compose up`)
  - How to run tests (`./mvnw test`)
  - API documentation link
  - Environment variables
  - Deployment guide

---

### ✅ Checkpoint Milestone 8

Kamu sudah bisa menjawab di interview:

- *"Gimana kamu tahu kalau production sistem kamu bermasalah?"*
- *"Jelaskan CI/CD pipeline kamu"*
- *"Gimana cara kamu manage secrets di production?"*
- *"Apa itu observability? Bedanya sama monitoring?"*

---

## 📋 Interview Cheat Sheet

Rangkuman quick answer untuk semua topik yang mungkin ditanyain:

### TDD & Testing

| Pertanyaan                | Jawaban Singkat                                                        |
| ------------------------- | ---------------------------------------------------------------------- |
| TDD workflow?             | Red → Green → Refactor                                               |
| @Mock vs @Spy?            | @Mock: dummy object sepenuhnya. @Spy: real object, bisa partial mock   |
| Unit vs Integration test? | Unit: isolasi, cepat. Integration: test komponen bersama, lebih lambat |

### Database

| Pertanyaan                         | Jawaban Singkat                                                                                           |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------- |
| 1NF, 2NF, 3NF?                     | 1: no repeating group. 2: no partial dependency. 3: no transitive dependency                              |
| Kapan denormalize?                 | Ketika read performance lebih kritis daripada storage cost dan write complexity                           |
| Optimistic vs Pessimistic locking? | Optimistic: versioning, cocok untuk low contention. Pessimistic: SELECT FOR UPDATE, untuk high contention |

### Race Condition & Idempotency

| Pertanyaan                | Jawaban Singkat                                                          |
| ------------------------- | ------------------------------------------------------------------------ |
| Race condition?           | Dua proses baca-tulis data yang sama secara bersamaan tanpa sinkronisasi |
| Idempotency?              | Operasi yang dipanggil N kali punya efek sama seperti dipanggil 1 kali   |
| Implementasi idempotency? | Idempotency-Key di header + cache hasil di Redis dengan TTL              |

### REST API

| Pertanyaan    | Jawaban Singkat                                                                         |
| ------------- | --------------------------------------------------------------------------------------- |
| PUT vs PATCH? | PUT: replace seluruh resource. PATCH: update sebagian field                             |
| 400 vs 422?   | 400: bad request syntax. 422: syntactically valid tapi semantically salah               |
| Stateless?    | Server tidak simpan state client. Setiap request harus punya semua info yang dibutuhkan |

### Docker & Kubernetes

| Pertanyaan                   | Jawaban Singkat                                                                               |
| ---------------------------- | --------------------------------------------------------------------------------------------- |
| Docker vs K8s?               | Docker: containerization. K8s: orchestration (scale, scheduling, self-healing)                |
| Liveness vs Readiness probe? | Liveness: apakah container perlu di-restart? Readiness: apakah container siap terima traffic? |
| Rolling update?              | Update pod satu per satu, tidak ada downtime                                                  |

---

## 🔗 Resources

- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/index.html)
- [Qdrant Java Client](https://github.com/qdrant/java-client)
- [Kubernetes by Example](https://kubernetesbyexample.com)
- [Martin Fowler — Idempotent Receiver](https://martinfowler.com/articles/patterns-of-distributed-systems/idempotent-receiver.html)
- [Use The Index, Luke (SQL indexing)](https://use-the-index-luke.com)

---

*Last updated: 2025 | Project: SmartHire Platform*
