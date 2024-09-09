package com.joanet.pizzalgustmobile

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * Esta interfaz define una API para el servicio web.
 */
interface ApiService {

    /* =========================================↓ LOGIN ↓============================ */
    /**
     * Este método realiza una solicitud POST para iniciar sesión en el sistema.
     *
     * @param requestBody Cuerpo de la solicitud que contiene los datos de inicio de sesión.
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */

    @Headers("Content-Type: application/json")
    @POST("pizzalgust/login")
    fun getUserData(@Body requestBody: Login): Call<Login>

    /**
     * Este método realiza una solicitud GET para obtener datos de usuarios.
     *
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */

    /* =========================================↓ CREATE USER ↓============================ */

    /**
     * Este método realiza una solicitud POST para crear un nuevo usuario en el sistema.
     *
     * @param requestBody Cuerpo de la solicitud que contiene los datos del nuevo usuario.
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */
    @Headers("Content-Type: application/json")
    @POST("pizzalgust/create-user")
    fun createUser(@Body requestBody: CreateUser): Call<UserResponse>

    /* =========================================↓ GET ALL USERS ↓============================ */

    /**
     * Este método realiza una solicitud POST para obtener todos los usuarios del sistema.
     * Solo funciona si la petición proviene de un usuario administrador.
     *
     * @param requestBody Cuerpo de la solicitud que contiene el token de autenticación.
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */

    @Headers("Content-Type: application/json")
    @POST("pizzalgust/get-all-users")
    fun getAllUsers(@Body requestBody: GetAllUsersRequest): Call<GetAllUsers>

    /* =========================================↓ UPDATE-USER ↓============================ */
    /**
     * Este método realiza una solicitud PUT para actualizar los datos de un usuario en el sistema.
     * Si la petición proviene de un usuario que no es admin, se actualizan los datos del propio usuario.
     * Si la petición proviene de un usuario administrador, es OBLIGATORIO enviar un parámetro user_id para saber los datos de qué usuario hay que actualizar.
     *
     * @param requestBody Cuerpo de la solicitud que contiene los datos del usuario a actualizar.
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */
    @Headers("Content-Type: application/json")
    @PUT("pizzalgust/update-user")
    fun updateUser(@Body requestBody: UpdateUser): Call<UpdateResponse>
    /* =========================================↓ UPDATE DESDE ADMIN ↓============================ */
    @Headers("Content-Type: application/json")
    @PUT("pizzalgust/update-user")
    fun updateAdminUser(@Body requestBody: AdminUpdateUser): Call<AdminUpdateResponse>

    /* =========================================↓ DELETE ↓============================ */
    /**
     * Este método realiza una solicitud DELETE para eliminar un usuario del sistema.
     * Solo funciona si la petición proviene de un usuario administrador.
     *
     * @param requestBody Cuerpo de la solicitud que contiene el token de autenticación y el ID del usuario a borrar.
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */

    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "pizzalgust/delete-user", hasBody = true)
    fun deleteUser(@Body requestBody: DeleteUser): Call<DeleteUserResponse>



    /* =========================================↓ LOGOOUT ↓============================ */
    /**
     * Esta interfaz define una API para realizar el cierre de sesión en el sistema.
     */
    interface ApiLogout {

        /**
         * Este método realiza una solicitud POST para cerrar sesión en el sistema.
         *
         * @param requestBody Cuerpo de la solicitud que contiene los datos de cierre de sesión.
         * @return Objeto Call que representa la llamada asíncrona y su respuesta.
         */

        @Headers("Content-type: application/json")
        @POST("pizzalgust/logout")

        fun logout(@Body requestBody: Logout): Call<Logout>

    }

    /* =========================================↓ PIZZAS ↓============================ */
    /**
     * Este método realiza una solicitud POST para crear una nueva pizza en el sistema.
     *
     * @param requestBody Cuerpo de la solicitud que contiene los datos de la nueva pizza.
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */
    @Headers("Content-type: application/json")
    @POST("pizzalgust/create-pizza")
    fun createPizza(@Body requestBody: CreatePizza): Call<ResponseCreatePizza>

    /* =========================================↓ LISTADO PIZZAS ↓============================ */
    /**
     * Este método realiza una solicitud POST para obtener todas las pizzas del sistema.
     *
     * @param getAllPizzasRequest Cuerpo de la solicitud que contiene los criterios de búsqueda.
     * @return Objeto Call que representa la llamada asíncrona y su respuesta.
     */
    @Headers("Content-type: application/json")
    @POST("pizzalgust/get-all-pizzas")
    fun getAllPizzas(@Body getAllPizzasRequest: GetAllPizzasRequest): Call<GetAllPizzas>
}