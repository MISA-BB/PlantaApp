package mx.edu.utez.plantasbackend.dto;


import mx.edu.utez.plantasbackend.model.Usuario;

public class AuthDtos {

    public static class LoginRequest {
        private String correo;
        private String password;

        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String fullName;
        private String correo;
        private String password;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String message;
        private Usuario user;

        public AuthResponse(String message, Usuario user) {
            this.message = message;
            this.user = user;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Usuario getUser() { return user; }
        public void setUser(Usuario user) { this.user = user; }
    }
}
