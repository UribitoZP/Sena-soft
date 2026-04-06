import 'dart:convert';
import 'package:http/http.dart' as http;

/// URL base del servidor REST Java.
/// Cambiar la IP a la del PC donde corre el escritorio.
/// En emulador Android: 10.0.2.2 apunta al localhost del host.
const String _baseUrl = 'http://10.0.2.2:8080';

class ApiService {
  static const Duration _timeout = Duration(seconds: 10);

  // ── Auth ────────────────────────────────────────────────────

  Future<Map<String, dynamic>> login(String usuario, String clave) async {
    final response = await http
        .post(
          Uri.parse('$_baseUrl/auth/login'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({'usuario': usuario, 'clave': clave, 'rol': ''}),
        )
        .timeout(_timeout);

    if (response.statusCode == 200) {
      return jsonDecode(response.body) as Map<String, dynamic>;
    }
    throw Exception('Credenciales incorrectas');
  }

  // ── Habitaciones ─────────────────────────────────────────────

  Future<List<Map<String, dynamic>>> getHabitaciones() async {
    final response = await http
        .get(Uri.parse('$_baseUrl/habitaciones'))
        .timeout(_timeout);

    if (response.statusCode == 200) {
      final list = jsonDecode(response.body) as List;
      return list.cast<Map<String, dynamic>>();
    }
    throw Exception('Error al cargar habitaciones');
  }

  // ── Reservas ─────────────────────────────────────────────────

  Future<List<Map<String, dynamic>>> getReservas() async {
    final response = await http
        .get(Uri.parse('$_baseUrl/reservas'))
        .timeout(_timeout);

    if (response.statusCode == 200) {
      final list = jsonDecode(response.body) as List;
      return list.cast<Map<String, dynamic>>();
    }
    throw Exception('Error al cargar reservas');
  }

  // ── Stats ─────────────────────────────────────────────────────

  Future<Map<String, dynamic>> getStats() async {
    final response = await http
        .get(Uri.parse('$_baseUrl/stats'))
        .timeout(_timeout);

    if (response.statusCode == 200) {
      return jsonDecode(response.body) as Map<String, dynamic>;
    }
    throw Exception('Error al cargar estadísticas');
  }
}
