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

// --- Metrics Models ---

data class SlaMetric(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("target_percentage") val targetPercentage: Double,
    @SerializedName("created_at") val createdAt: String?
)

data class SlaRecord(
    @SerializedName("id") val id: Int,
    @SerializedName("sla_metric_id") val slaMetricId: Int,
    @SerializedName("profile_id") val profileId: String,
    @SerializedName("record_date") val recordDate: String,
    @SerializedName("actual_percentage") val actualPercentage: Double,
    @SerializedName("details") val details: String,
    @SerializedName("created_at") val createdAt: String?
)

// --- Security Models ---

data class SecurityStats(
    @SerializedName("success_count") val successCount: Int,
    @SerializedName("failed_count") val failedCount: Int,
    @SerializedName("owasp_compliance_score") val owaspScore: Int, // e.g., 6 (out of 6)
    @SerializedName("total_controls") val totalControls: Int
)

data class SecurityAlert(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("is_critical") val isCritical: Boolean,
    @SerializedName("timestamp") val timestamp: String
)

data class AccessLog(
    @SerializedName("id") val id: Int,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("ip_address") val ipAddress: String,
    @SerializedName("device") val device: String,
    @SerializedName("is_success") val isSuccess: Boolean,
    @SerializedName("timestamp") val timestamp: String
)

data class OwaspConfig(
    @SerializedName("id") val id: Int,
    @SerializedName("control_name") val controlName: String,
    @SerializedName("description") val description: String,
    @SerializedName("is_enabled") val isEnabled: Boolean
)