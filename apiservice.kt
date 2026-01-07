package com.example.myapplication

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String,
        @Field("role") role: String,
        @Field("full_name") fullName: String
    ): Call<ApiResponse>
    @FormUrlEncoded
    @POST("add_doctor.php")
    fun addDoctor(
        @Field("doctor_id_code") doctorId: String,
        @Field("full_name") fullName: String,
        @Field("hospital_name") hospitalName: String,
        @Field("specialist") specialist: String,
        @Field("experience_years") experienceYears: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): Call<ApiResponse>
    @FormUrlEncoded
    @POST("update_credentials.php")
    fun updateCredentials(
        @Field("username") currentUsername: String,
        @Field("current_password") currentPassword: String,
        @Field("new_username") newUsername: String,
        @Field("new_password") newPassword: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("add_patient.php")
    fun addPatient(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String,
        @Field("full_name") fullName: String,
        @Field("patient_id_code") patientId: String,
        @Field("pregnancy_week") week: String,
        @Field("mobile_number") mobile: String,
        @Field("health_issues") healthIssues: String,
        @Field("added_by") addedBy: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("get_doctor_patients.php")
    fun getDoctorPatients(
        @Field("doctor_username") doctorUsername: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("update_patient.php")
    fun updatePatient(
        @Field("patient_id") patientId: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("full_name") fullName: String,
        @Field("patient_id_code") patientIdCode: String,
        @Field("pregnancy_week") pregnancyWeek: String,
        @Field("mobile_number") mobileNumber: String,
        @Field("health_issues") healthIssues: String,
        @Field("caretaker_name") caretakerName: String = "",
        @Field("caretaker_relation") caretakerRelation: String = "",
        @Field("caretaker_mobile") caretakerMobile: String = ""
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("forgot_password.php")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("get_patient_profile.php")
    fun getPatientProfile(
        @Field("username") username: String
    ): Call<ApiResponse>

    @POST("get_all_doctors.php")
    fun getAllDoctors(): Call<ApiResponse>

    @FormUrlEncoded
    @POST("update_doctor.php")
    fun updateDoctor(
        @Field("doctor_id") doctorId: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("full_name") fullName: String,
        @Field("doctor_id_code") doctorIdCode: String,
        @Field("hospital_name") hospitalName: String,
        @Field("specialist") specialist: String,
        @Field("experience_years") experienceYears: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("get_doctor_profile.php")
    fun getDoctorProfile(
        @Field("username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("save_anaemia_record.php")
    fun saveAnaemiaRecord(
        @Field("username") username: String,
        @Field("red_pixel") red: Float,
        @Field("green_pixel") green: Float,
        @Field("blue_pixel") blue: Float,
        @Field("hb_level") hb: Float,
        @Field("severity") severity: String,
        @Field("symptoms") symptoms: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("get_anaemia_history.php")
    fun getAnaemiaHistory(
        @Field("username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("share_report.php")
    fun shareReport(
        @Field("patient_username") username: String,
        @Field("hb_level") hb: Float,
        @Field("severity") severity: String,
        @Field("pregnancy_week") week: String,
        @Field("red_pixel") red: Float,
        @Field("green_pixel") green: Float,
        @Field("blue_pixel") blue: Float
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("share_report.php")
    fun shareHypertensionReport(
        @Field("patient_username") username: String,
        @Field("systolic") systolic: Int,
        @Field("diastolic") diastolic: Int,
        @Field("severity") severity: String,
        @Field("report_type") reportType: String = "hypertension"
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("share_report.php")
    fun shareFetalGrowthReport(
        @Field("patient_username") username: String,
        @Field("fetal_weight") weight: Int,
        @Field("pregnancy_week") week: Int,
        @Field("severity") severity: String,
        @Field("report_type") reportType: String = "fetal_growth"
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("get_doctor_alerts.php")
    fun getDoctorAlerts(
        @Field("doctor_username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("get_unread_doctor_alerts.php")
    fun getUnreadDoctorAlerts(
        @Field("doctor_username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("mark_alert_read.php")
    fun markAlertRead(
        @Field("alert_id") alertId: Int
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("mark_alert_notified.php")
    fun markAlertNotified(
        @Field("alert_id") alertId: Int
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("update_fcm_token.php")
    fun updateFcmToken(
        @Field("username") username: String,
        @Field("fcm_token") token: String
    ): Call<ApiResponse>

    @Multipart
    @POST("upload_profile_image.php")
    fun uploadProfileImage(
        @Part profile_image: okhttp3.MultipartBody.Part,
        @Part("username") username: okhttp3.RequestBody
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("save_prescription.php")
    fun savePrescription(
        @Field("doctor_username") doctorUsername: String,
        @Field("patient_username") patientUsername: String,
        @Field("medicines") medicinesJson: String
    ): Call<ApiResponse>

    @GET("get_patient_prescriptions.php")
    fun getPrescriptions(
        @Query("username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("save_hypertension_record.php")
    fun saveHypertensionRecord(
        @Field("username") username: String,
        @Field("systolic") systolic: Int,
        @Field("diastolic") diastolic: Int,
        @Field("blood_sugar") sugar: Float,
        @Field("body_temp") temp: Float,
        @Field("heart_rate") rate: Int,
        @Field("symptoms") symptoms: String
    ): Call<ApiResponse>

    @GET("get_hypertension_history.php")
    fun getHypertensionHistory(
        @Query("username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("save_fetal_growth_record.php")
    fun saveFetalGrowthRecord(
        @Field("username") username: String,
        @Field("gestational_age") ga: Int,
        @Field("fetal_weight") weight: Int,
        @Field("severity") severity: String
    ): Call<ApiResponse>

    @GET("get_fetal_growth_history.php")
    fun getFetalGrowthHistory(
        @Query("username") username: String
    ): Call<ApiResponse>

    @GET("get_unread_patient_alerts.php")
    fun getUnreadPatientAlerts(
        @Query("username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("mark_patient_alerts_read.php")
    fun markPatientAlertsRead(
        @Field("username") username: String
    ): Call<ApiResponse>

    @GET("get_all_medical_history.php")
    fun getAllMedicalHistory(
        @Query("username") username: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("send_doctor_message.php")
    fun sendDoctorMessage(
        @Field("doctor_username") doctor: String,
        @Field("patient_username") patient: String,
        @Field("alert_type") type: String,
        @Field("message") message: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("send_patient_reply.php")
    fun sendPatientReply(
        @Field("doctor_username") doctor: String,
        @Field("patient_username") patient: String,
        @Field("message") message: String
    ): Call<ApiResponse>
    @FormUrlEncoded
    @POST("delete_doctor_alert.php")
    fun deleteDoctorAlert(
        @Field("alert_id") alertId: Int
    ): Call<ApiResponse>
}
