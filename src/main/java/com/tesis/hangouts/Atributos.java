package com.tesis.hangouts;

import java.util.List;

public class Atributos {
        private String fecha;
        private String diferenciaHoras;
        private String rol;
        private List<Double> roles_autodefinidos;
        private String rol_companeros;

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getDiferenciaHoras() {
            return diferenciaHoras;
        }

        public void setDiferenciaHoras(String diferenciaHoras) {
            this.diferenciaHoras = diferenciaHoras;
        }

        public List<Double> getRolesAutodefinidos() {
            return roles_autodefinidos;
        }

        public void setRolesAutodefinidos(List<Double> roles) {
            this.roles_autodefinidos = roles;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }

        public String getRolCompaneros() {
            return rol_companeros;
        }

        public void setRolCompaneros(String rol_companeros) {
            this.rol_companeros = rol_companeros;
        }

}
