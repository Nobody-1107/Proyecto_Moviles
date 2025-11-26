package com.example.proyectomoviles.model

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id") val id: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("position") val position: String,
    @SerializedName("department_id") val departmentId: Int?,
    @SerializedName("role") val role: String,
    @SerializedName("is_available_for_change") val isAvailableForChange: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("profile_skills") val profileSkills: List<ProfileSkill>?
)

data class Skill(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class Vacancy(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("department_id") val departmentId: Int?,
    @SerializedName("created_by") val createdBy: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("vacancy_skills") val vacancySkills: List<VacancySkill>?
)

data class Department(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("created_at") val createdAt: String?
)

data class ProfileSkill(
    @SerializedName("profile_id") val profileId: String,
    @SerializedName("skill_id") val skillId: Int
)

data class VacancySkill(
    @SerializedName("vacancy_id") val vacancyId: Int,
    @SerializedName("skill_id") val skillId: Int
)
