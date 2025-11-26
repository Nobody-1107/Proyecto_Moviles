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
}