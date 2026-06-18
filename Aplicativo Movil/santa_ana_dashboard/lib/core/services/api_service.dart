import 'dart:convert';
import 'dart:io';
import 'package:shared_preferences/shared_preferences.dart';

class ApiService {
  static const Duration _timeout = Duration(seconds: 10);
  static const String _ipKey = 'server_ip';
  static const String _tokenKey = 'auth_token';

  static String serverIp = '10.0.2.2';
  static String? _token;

  static Future<void> loadSavedIp() async {
    final prefs = await SharedPreferences.getInstance();
    final saved = prefs.getString(_ipKey);
    if (saved != null && saved.isNotEmpty) {
      serverIp = saved;
    }
  }

  static Future<void> saveIp(String ip) async {
    serverIp = ip;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_ipKey, ip);
  }

  static void setToken(String token) {
    _token = token;
  }

  static String get _baseUrl => 'https://$serverIp:8443';

  HttpClient _createClient() {
    return HttpClient()
      ..badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
  }

  Future<Map<String, dynamic>> _request(
    String method, String path, {Object? body}) async {
    final client = _createClient();
    try {
      final uri = Uri.parse('$_baseUrl$path');
      final request = await client.openUrl(method, uri);
      request.headers.contentType = ContentType.json;
      if (_token != null) {
        request.headers.set('Authorization', 'Bearer $_token');
      }
      if (body != null) {
        request.write(jsonEncode(body));
      }
      final response = await request.close().timeout(_timeout);
      final bodyStr = await response.transform(utf8.decoder).join();
      if (response.statusCode == 200) {
        return jsonDecode(bodyStr) as Map<String, dynamic>;
      }
      if (response.statusCode == 401) {
        throw Exception('Token expirado');
      }
      throw Exception('Error: ${response.statusCode}');
    } finally {
      client.close();
    }
  }

  Future<List<Map<String, dynamic>>> _requestList(String path) async {
    final client = _createClient();
    try {
      final uri = Uri.parse('$_baseUrl$path');
      final request = await client.getUrl(uri);
      request.headers.contentType = ContentType.json;
      if (_token != null) {
        request.headers.set('Authorization', 'Bearer $_token');
      }
      final response = await request.close().timeout(_timeout);
      final body = await response.transform(utf8.decoder).join();
      if (response.statusCode == 200) {
        final list = jsonDecode(body) as List;
        return list.cast<Map<String, dynamic>>();
      }
      throw Exception('Error: ${response.statusCode}');
    } finally {
      client.close();
    }
  }

  Future<Map<String, dynamic>> login(
      String usuario, String clave) async {
    final result = await _request('POST', '/auth/login',
        body: {'usuario': usuario, 'clave': clave, 'rol': ''});
    if (result.containsKey('token')) {
      setToken(result['token'] as String);
    }
    return result;
  }

  Future<List<Map<String, dynamic>>> getHabitaciones() =>
      _requestList('/habitaciones');

  Future<List<Map<String, dynamic>>> getReservas() =>
      _requestList('/reservas');

  Future<Map<String, dynamic>> getStats() => _request('GET', '/stats');

  Future<Map<String, dynamic>> getReportes() => _request('GET', '/reportes');
}
