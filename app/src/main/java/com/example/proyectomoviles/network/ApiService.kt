package com.example.proyectomoviles.network

import com.example.proyectomoviles.model.*
import retrofit2.http.*

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
    suspend fun deleteProfile(@Path("id") id: String)

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

    // VACANCY SKILLS
    @GET("api/vacancyskills/by-vacancy/{vacancyId}")
    suspend fun getVacancySkills(@Path("vacancyId") vacancyId: Int): List<VacancySkill>
    
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
}