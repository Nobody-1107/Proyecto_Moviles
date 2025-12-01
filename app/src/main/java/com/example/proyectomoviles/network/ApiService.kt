package com.example.proyectomoviles.network

import com.example.proyectomoviles.model.*
import retrofit2.Response
import retrofit2.http.*
import com.example.proyectomoviles.model.LoginRequest

interface ApiService {

    // PROFILES
    @GET("api/profiles")
    suspend fun getProfiles(): List<Profile>

    @GET("api/profiles/{id}")
    suspend fun getProfileById(@Path("id") id: String): Profile

    @POST("api/profiles")
    suspend fun createProfile(@Body profile: Profile): Profile

    @PUT("api/profiles/{id}")
    suspend fun updateProfile(@Path("id") id: String, @Body profile: Profile): Profile

    @DELETE("api/profiles/{id}")
    suspend fun deleteProfile(@Path("id") id: String): Response<Void>

    // SKILLS
    @GET("api/skills")
    suspend fun getSkills(): List<Skill>

    @GET("api/skills/{id}")
    suspend fun getSkillById(@Path("id") id: Int): Skill

    // VACANCIES
    @GET("api/vacancies")
    suspend fun getVacancies(): List<Vacancy>

    @GET("api/vacancies/{id}")
    suspend fun getVacancyById(@Path("id") id: Int): Vacancy
    
    @POST("api/vacancies")
    suspend fun createVacancy(@Body vacancy: Vacancy): Vacancy

    @PUT("api/vacancies/{id}")
    suspend fun updateVacancy(@Path("id") id: Int, @Body vacancy: Vacancy): Response<Vacancy>

    @DELETE("api/vacancies/{id}")
    suspend fun deleteVacancy(@Path("id") id: Int): Response<Void>

    // SUGGESTIONS
    @GET("api/suggestion") // CORREGIDO: Cambiado de plural a singular
    suspend fun getSuggestions(): List<Suggestion>

    // Asignar candidato (Endpoint hipotético para la funcionalidad)
    @POST("api/vacancies/{vacancyId}/candidates")
    suspend fun addCandidateToVacancy(@Path("vacancyId") vacancyId: Int, @Body profileId: String)

    // DEPARTMENTS
    @GET("api/departments")
    suspend fun getDepartments(): List<Department>

    @GET("api/departments/{id}")
    suspend fun getDepartmentById(@Path("id") id: Int): Department

    // PROFILE SKILLS
    @GET("api/profileskills/by-profile/{profileId}")
    suspend fun getProfileSkills(@Path("profileId") profileId: String): List<ProfileSkill>

    @POST("api/profileskills")
    suspend fun createProfileSkill(@Body profileSkill: ProfileSkill): ProfileSkill

    @HTTP(method = "DELETE", path = "api/profileskills", hasBody = true)
    suspend fun deleteProfileSkill(@Body body: Map<String, String>): Response<Void>

    // VACANCY SKILLS
    @GET("api/vacancyskills/by-vacancy/{vacancyId}")
    suspend fun getVacancySkills(@Path("vacancyId") vacancyId: Int): List<VacancySkill>
    
    @POST("api/vacancyskills")
    suspend fun createVacancySkill(@Body vacancySkill: VacancySkill)

    // --- SECURITY ENDPOINTS ---
    
    @GET("api/security/stats")
    suspend fun getSecurityStats(): SecurityStats
    
    @GET("api/security/alerts")
    suspend fun getSecurityAlerts(): List<SecurityAlert>
    
    @GET("api/security/logs")
    suspend fun getAccessLogs(): List<AccessLog>
    
    @GET("api/security/owasp-config")
    suspend fun getOwaspConfig(): List<OwaspConfig>
    
    @PUT("api/security/owasp-config/{id}")
    suspend fun updateOwaspConfig(@Path("id") id: Int, @Body config: OwaspConfig)

    // --- SLA METRICS ENDPOINTS ---
    @GET("api/metrics/sla")
    suspend fun getSlaMetrics(): List<SlaMetric>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Profile

    @GET("api/Sugerencias?select=*,profiles(*)")
    suspend fun getSugerencias(): List<Sugerencia>

    @DELETE("api/Sugerencias/{id}")
    suspend fun deleteSugerencia(@Path("id") id: Long): Response<Void>
}