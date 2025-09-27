#!/usr/bin/env python3
"""
Mock Server para simular el backend de clases
Sirve el JSON de prueba en el endpoint /api/classes/
"""

from http.server import HTTPServer, BaseHTTPRequestHandler
import json
import urllib.parse

# JSON de prueba con 5 clases
MOCK_DATA = {
    "success": True,
    "message": "Clases obtenidas correctamente",
    "classes": [
        {
            "class_id": "1",
            "professor_first_name": "Ana",
            "professor_last_name": "García",
            "gym_name": "Gimnasio Central",
            "gym_city": "Buenos Aires",
            "gym_address": "Av. Corrientes 1234",
            "class_discipline_name": "Yoga",
            "class_scheduled_at": "2025-09-25 08:00:00",
            "class_max_participants": 15
        },
        {
            "class_id": "2",
            "professor_first_name": "Carlos",
            "professor_last_name": "Rodríguez",
            "gym_name": "Urban Gym Palermo",
            "gym_city": "Buenos Aires",
            "gym_address": "Palermo 456",
            "class_discipline_name": "Spinning",
            "class_scheduled_at": "2025-09-25 18:30:00",
            "class_max_participants": 18
        },
        {
            "class_id": "3",
            "professor_first_name": "María",
            "professor_last_name": "López",
            "gym_name": "FitZone Córdoba",
            "gym_city": "Córdoba",
            "gym_address": "San Martín 567",
            "class_discipline_name": "CrossFit",
            "class_scheduled_at": "2025-09-25 19:00:00",
            "class_max_participants": 20
        },
        {
            "class_id": "4",
            "professor_first_name": "Juan",
            "professor_last_name": "Pérez",
            "gym_name": "PowerGym Rosario",
            "gym_city": "Rosario",
            "gym_address": "Mitre 890",
            "class_discipline_name": "Pilates",
            "class_scheduled_at": "2025-09-26 07:30:00",
            "class_max_participants": 12
        },
        {
            "class_id": "5",
            "professor_first_name": "Laura",
            "professor_last_name": "Fernández",
            "gym_name": "Fitness Club Mendoza",
            "gym_city": "Mendoza",
            "gym_address": "San Juan 321",
            "class_discipline_name": "Zumba",
            "class_scheduled_at": "2025-09-26 20:00:00",
            "class_max_participants": 25
        }
    ]
}

class MockHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Authorization, Content-Type')
        self.end_headers()

    def _set_error_headers(self, code):
        self.send_response(code)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()

    def do_OPTIONS(self):
        """Handle preflight requests"""
        self._set_headers()

    def do_GET(self):
        """Handle GET requests"""
        parsed_path = urllib.parse.urlparse(self.path)
        path = parsed_path.path

        print(f"📱 Petición GET recibida: {path}")

        # Verificar token de autorización (simulado)
        auth_header = self.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            print("❌ Token de autorización faltante o inválido")
            self._set_error_headers(401)
            error_response = {
                "success": False,
                "message": "Token de autorización requerido"
            }
            self.wfile.write(json.dumps(error_response).encode())
            return

        # Endpoint para obtener clases
        if path == '/api/classes/' or path == '/api/classes':
            print("✅ Sirviendo datos de clases mock")
            self._set_headers()
            self.wfile.write(json.dumps(MOCK_DATA).encode())
            return

        # Endpoint no encontrado
        print(f"❌ Endpoint no encontrado: {path}")
        self._set_error_headers(404)
        error_response = {
            "success": False,
            "message": f"Endpoint {path} no encontrado"
        }
        self.wfile.write(json.dumps(error_response).encode())

    def log_message(self, format, *args):
        """Override to customize log format"""
        print(f"🌐 {self.address_string()} - {format % args}")

def run_mock_server(port=5000):
    """Ejecuta el servidor mock"""
    server_address = ('', port)
    httpd = HTTPServer(server_address, MockHandler)

    print(f"🚀 Mock Server iniciado en puerto {port}")
    print(f"📡 URL base: http://localhost:{port}/")
    print(f"🎯 Endpoint de clases: http://localhost:{port}/api/classes/")
    print(f"📱 Para emulador Android: http://10.0.2.2:{port}/api/classes/")
    print(f"🔧 Para dispositivo físico: http://192.168.0.X:{port}/api/classes/")
    print("👤 El servidor requiere un token Bearer (cualquier valor funciona)")
    print("\n💡 Para probar desde navegador:")
    print(f"   curl -H 'Authorization: Bearer test-token' http://localhost:{port}/api/classes/")
    print("\n⏹️  Presiona Ctrl+C para detener el servidor\n")

    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n🛑 Deteniendo mock server...")
        httpd.server_close()

if __name__ == '__main__':
    run_mock_server()
