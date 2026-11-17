import json
import os

collection = {
    "info": {
        "_postman_id": "e7b1a2c3-9876-4321-bcde-55a0f1e2d3c4",
        "name": "DonaTrack - Suite E2E Completa del Sistema",
        "description": "Colección completa e integrada para probar los 4 microservicios de DonaTrack (Donaciones, Incentivos, Notificaciones y Logística), tanto de manera individual por servicio como a través del flujo End-to-End integral con extracción dinámica de variables.",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "variable": [
        {"key": "baseUrl_donaciones", "value": "http://localhost:8080", "type": "string"},
        {"key": "baseUrl_incentivos", "value": "http://localhost:8081", "type": "string"},
        {"key": "baseUrl_notificaciones", "value": "http://localhost:8082", "type": "string"},
        {"key": "baseUrl_logistica", "value": "http://localhost:8083", "type": "string"},
        {"key": "categoriaId", "value": "1", "type": "string"},
        {"key": "subcategoriaId", "value": "1", "type": "string"},
        {"key": "donanteId", "value": "1", "type": "string"},
        {"key": "entidadId", "value": "1", "type": "string"},
        {"key": "donacionId", "value": "1", "type": "string"},
        {"key": "matcheoId", "value": "1", "type": "string"},
        {"key": "necesidadId", "value": "1", "type": "string"},
        {"key": "camionId", "value": "1", "type": "string"},
        {"key": "rutaId", "value": "1", "type": "string"},
        {"key": "entregaId", "value": "1", "type": "string"}
    ],
    "item": []
}

from urllib.parse import urlparse

def parse_url(url_str):
    url_str = url_str.replace('{{baseUrl_donaciones}}', 'http://localhost:8080')
    url_str = url_str.replace('{{baseUrl_incentivos}}', 'http://localhost:8081')
    url_str = url_str.replace('{{baseUrl_notificaciones}}', 'http://localhost:8082')
    url_str = url_str.replace('{{baseUrl_logistica}}', 'http://localhost:8083')
    
    parsed = urlparse(url_str)
    path_segments = [p for p in parsed.path.split('/') if p]
    obj = {
        'raw': url_str,
        'protocol': parsed.scheme or 'http',
        'host': [parsed.hostname or 'localhost'],
    }
    if parsed.port:
        obj['port'] = str(parsed.port)
    if path_segments:
        obj['path'] = path_segments
    if parsed.query:
        query_params = []
        for q in parsed.query.split('&'):
            if '=' in q:
                k, v = q.split('=', 1)
                query_params.append({'key': k, 'value': v})
            else:
                query_params.append({'key': q, 'value': ''})
        obj['query'] = query_params
    return obj

def make_request(name, method, url_str, headers=None, body_obj=None, raw_body=None, test_script=None, desc=""):
    req = {
        "name": name,
        "request": {
            "method": method,
            "header": headers if headers is not None else [],
            "url": parse_url(url_str),
            "description": desc
        },
        "response": []
    }
    
    if body_obj is not None:
        if not any(h.get("key", "").lower() == "content-type" for h in req["request"]["header"]):
            req["request"]["header"].append({"key": "Content-Type", "value": "application/json"})
        req["request"]["body"] = {
            "mode": "raw",
            "raw": json.dumps(body_obj, indent=2, ensure_ascii=False)
        }
    elif raw_body is not None:
        req["request"]["body"] = {
            "mode": "raw",
            "raw": raw_body
        }
        
    if test_script:
        req["event"] = [
            {
                "listen": "test",
                "script": {
                    "type": "text/javascript",
                    "exec": test_script
                }
            }
        ]
        
    return req

# ==============================================================================
# FOLDER 0: Health Checks
# ==============================================================================
folder_0 = {
    "name": "00. Health & Conectividad",
    "item": [
        make_request(
            "Health Check - Donaciones Service (8080)",
            "GET",
            "{{baseUrl_donaciones}}/",
            desc="Verifica que el servicio de Donaciones esté corriendo en el puerto 8080.",
            test_script=[
                "pm.test('Status code is 200', function () {",
                "    pm.response.to.have.status(200);",
                "});",
                "pm.test('Responde mensaje de bienvenida', function () {",
                "    pm.expect(pm.response.text()).to.include('Donaciones');",
                "});"
            ]
        ),
        make_request(
            "Health Check - Incentivos Service (8081)",
            "GET",
            "{{baseUrl_incentivos}}/incentivos/1/metricas",
            desc="Verifica que el servicio de Incentivos responda en el puerto 8081.",
            test_script=[
                "pm.test('Status code is 200', function () {",
                "    pm.response.to.have.status(200);",
                "});"
            ]
        ),
        make_request(
            "Health Check - Logistica Service (8083)",
            "GET",
            "{{baseUrl_logistica}}/logistica-service/camiones",
            desc="Verifica que el servicio de Logística responda en el puerto 8083.",
            test_script=[
                "pm.test('Status code is 200', function () {",
                "    pm.response.to.have.status(200);",
                "});"
            ]
        )
    ]
}

# ==============================================================================
# FOLDER 1: Donaciones Service
# ==============================================================================
folder_1 = {
    "name": "01. Donaciones Service (Puerto 8080)",
    "item": [
        {
            "name": "Categorías y Subcategorías",
            "item": [
                make_request(
                    "Listar Categorías",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/categorias",
                    desc="Obtiene todas las categorías cargadas en el sistema (ej. Alimentos, Vestimenta, Mobiliario)."
                ),
                make_request(
                    "Crear Categoría",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/categorias",
                    body_obj={"nombre": "Higiene y Limpieza"},
                    desc="Crea una nueva categoría de donaciones.",
                    test_script=[
                        "pm.test('Status code is 200 or 201', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                        "});",
                        "var jsonData = pm.response.json();",
                        "if (jsonData.id) {",
                        "    pm.collectionVariables.set('categoriaId', jsonData.id);",
                        "}"
                    ]
                ),
                make_request(
                    "Obtener Categoría por ID",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/categorias/{{categoriaId}}",
                    desc="Obtiene el detalle de una categoría por su identificador."
                ),
                make_request(
                    "Listar Subcategorías de una Categoría",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/categorias/{{categoriaId}}/subcategorias",
                    desc="Obtiene las subcategorías que pertenecen a la categoría especificada."
                ),
                make_request(
                    "Listar Todas las Subcategorías",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/subcategorias",
                    desc="Obtiene todas las subcategorías registradas."
                ),
                make_request(
                    "Crear Subcategoría",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/subcategorias",
                    body_obj={
                        "nombre": "Jabón Líquido",
                        "categoriaId": 1,
                        "unidadDeMedida": "L"
                    },
                    desc="Crea una nueva subcategoría indicando a qué categoría pertenece.",
                    test_script=[
                        "pm.test('Status code is 200 or 201', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                        "});",
                        "var jsonData = pm.response.json();",
                        "if (jsonData.id) {",
                        "    pm.collectionVariables.set('subcategoriaId', jsonData.id);",
                        "}"
                    ]
                ),
                make_request(
                    "Obtener Subcategoría por ID",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/subcategorias/{{subcategoriaId}}",
                    desc="Obtiene los datos de una subcategoría por ID."
                )
            ]
        },
        {
            "name": "Donantes",
            "item": [
                make_request(
                    "Listar Todos los Donantes",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/donantes",
                    desc="Devuelve la lista de donantes humanos y jurídicos registrados."
                ),
                make_request(
                    "Crear Donante Persona Humana",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/donantes",
                    body_obj={
                        "tipo": "HUMANA",
                        "nombre": "Esteban",
                        "apellido": "Quito",
                        "fechaDeNacimiento": "1992-04-10",
                        "genero": "Masculino",
                        "numeroDeDocumento": "36895123",
                        "contactos": [
                            {"tipo": "EMAIL", "valor": "esteban.quito@example.com"},
                            {"tipo": "WHATSAPP", "valor": "+5491122334455"}
                        ]
                    },
                    desc="Registra un donante humano. Donaciones automáticamente sincroniza con Incentivos (POST /donante).",
                    test_script=[
                        "pm.test('Donante creado exitosamente', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                        "});",
                        "var jsonData = pm.response.json();",
                        "if (jsonData.id) {",
                        "    pm.collectionVariables.set('donanteId', jsonData.id);",
                        "}"
                    ]
                ),
                make_request(
                    "Crear Donante Persona Jurídica",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/donantes",
                    body_obj={
                        "tipo": "JURIDICA",
                        "razonSocial": "Distribuidora Mayorista SA",
                        "tipoJuridica": "EMPRESA",
                        "rubro": "Distribución de Alimentos",
                        "contactos": [
                            {"tipo": "EMAIL", "valor": "contacto@distribuidorasa.com"},
                            {"tipo": "SMS", "valor": "+5491155443322"}
                        ],
                        "representantes": []
                    },
                    desc="Registra un donante jurídico con razón social y rubro."
                ),
                make_request(
                    "Obtener Donante por ID",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/donantes/{{donanteId}}",
                    desc="Obtiene los detalles del donante creado."
                )
            ]
        },
        {
            "name": "Entidades Beneficiarias y Necesidades",
            "item": [
                make_request(
                    "Listar Entidades Beneficiarias",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/entidades-beneficiarias",
                    desc="Obtiene el listado de todas las entidades beneficiarias registradas (comedores, hogares, ONGs)."
                ),
                make_request(
                    "Crear Entidad Beneficiaria",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/entidades-beneficiarias",
                    body_obj={
                        "razonSocial": "Comedor Caritas Felices",
                        "tipoEntidad": "COMEDOR",
                        "calle": "Av. San Martín",
                        "altura": 450,
                        "contactos": [
                            {"tipo": "EMAIL", "valor": "caritasfelices@gmail.com"},
                            {"tipo": "WHATSAPP", "valor": "+5491199887766"}
                        ],
                        "representantes": []
                    },
                    desc="Crea una nueva entidad beneficiaria con dirección y medios de contacto.",
                    test_script=[
                        "pm.test('Entidad creada exitosamente', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                        "});",
                        "var jsonData = pm.response.json();",
                        "if (jsonData.id) {",
                        "    pm.collectionVariables.set('entidadId', jsonData.id);",
                        "}"
                    ]
                ),
                make_request(
                    "Obtener Entidad Beneficiaria por ID",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/entidades-beneficiarias/{{entidadId}}",
                    desc="Obtiene el detalle de una entidad beneficiaria y sus necesidades asociadas."
                ),
                make_request(
                    "Agregar Necesidad a Entidad",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/entidades-beneficiarias/{{entidadId}}/necesidades",
                    body_obj={
                        "subcategoriaId": 1,
                        "subcategoria": "Arroz",
                        "cantidad": 50.0,
                        "unidadDeMedida": "KG",
                        "descripcion": "Necesitamos arroz para el menú de los almuerzos",
                        "tipoNecesidad": "EXTRAORDINARIA",
                        "periodo": None,
                        "inicioRecurrencia": None
                    },
                    desc="Carga una necesidad extraordinaria o recurrente a la entidad para que pueda matchear con donaciones.",
                    test_script=[
                        "pm.test('Necesidad agregada', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 201, 204]);",
                        "});"
                    ]
                )
            ]
        },
        {
            "name": "Donaciones y Matchmaking",
            "item": [
                make_request(
                    "Listar Todas las Donaciones",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/donaciones",
                    desc="Obtiene todas las donaciones registradas con su historial de estados y bienes."
                ),
                make_request(
                    "Crear Nueva Donación",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/donaciones",
                    body_obj={
                        "idDonante": 1,
                        "descripcionGeneral": "Donación de alimentos secos",
                        "bienes": [
                            {
                                "nombre": "Arroz Blanco Grano Largo",
                                "descripcion": "Bolsa de arroz 5kg",
                                "cantidad": 10.0,
                                "unidadDeMedida": "KG",
                                "subcategoriaId": 1,
                                "estado": "NUEVO",
                                "fechaVencimiento": "2027-06-30",
                                "urlFoto": "https://example.com/arroz.jpg"
                            }
                        ]
                    },
                    desc="Crea la donación en estado DEPOSITO y la comunica automáticamente a Incentivos.",
                    test_script=[
                        "pm.test('Donación creada exitosamente', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                        "});",
                        "var jsonData = pm.response.json();",
                        "if (Array.isArray(jsonData) && jsonData.length > 0) {",
                        "    pm.collectionVariables.set('donacionId', jsonData[0].id);",
                        "} else if (jsonData.id) {",
                        "    pm.collectionVariables.set('donacionId', jsonData.id);",
                        "}"
                    ]
                ),
                make_request(
                    "Listar Donaciones de un Donante",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/donaciones/donante/{{donanteId}}",
                    desc="Obtiene el historial de donaciones de un donante específico."
                ),
                make_request(
                    "Actualizar Estado de Donación Manualmente",
                    "PATCH",
                    "{{baseUrl_donaciones}}/donaciones-service/donaciones/{{donacionId}}/estado",
                    body_obj={
                        "nuevoEstado": "DEPOSITO",
                        "motivo": "Verificación física en depósito",
                        "fechaNuevoEstado": "2026-09-20",
                        "autorDelCambio": "Operador Depósito"
                    },
                    desc="Permite forzar un cambio de estado válido según la máquina de estados de Donación."
                ),
                make_request(
                    "Consultar Sugerencias del Matcheador",
                    "GET",
                    "{{baseUrl_donaciones}}/donaciones-service/donaciones/matcheador",
                    desc="Ejecuta el algoritmo de matcheo y devuelve las sugerencias calculadas basadas en compatibilidad y subatención.",
                    test_script=[
                        "pm.test('Sugerencias obtenidas', function () {",
                        "    pm.response.to.have.status(200);",
                        "});",
                        "var jsonData = pm.response.json();",
                        "if (Array.isArray(jsonData) && jsonData.length > 0) {",
                        "    pm.collectionVariables.set('matcheoId', jsonData[0].id);",
                        "    if (jsonData[0].necesidadesSugeridas && jsonData[0].necesidadesSugeridas.length > 0) {",
                        "        pm.collectionVariables.set('necesidadId', jsonData[0].necesidadesSugeridas[0].id);",
                        "    }",
                        "}"
                    ]
                ),
                make_request(
                    "Seleccionar Match (Confirmar Asignación)",
                    "POST",
                    "{{baseUrl_donaciones}}/donaciones-service/donaciones/matcheador/{{matcheoId}}/{{necesidadId}}",
                    body_obj={
                        "autorDelCambio": "Administrador"
                    },
                    desc="El administrador confirma la asignación sugerida. Esto dispara: 1) Cambio de estado a ASIGNACION_REALIZADA, 2) PUT a Incentivos, 3) POST /logistica-service/recepcion/asignacion, 4) POST a Notificaciones.",
                    test_script=[
                        "pm.test('Match confirmado exitosamente', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 204]);",
                        "});"
                    ]
                )
            ]
        }
    ]
}

# ==============================================================================
# FOLDER 2: Incentivos Service
# ==============================================================================
folder_2 = {
    "name": "02. Incentivos Service (Puerto 8081)",
    "item": [
        make_request(
            "Registrar Donante en Incentivos",
            "POST",
            "{{baseUrl_incentivos}}/donante",
            body_obj={
                "id": 1,
                "contactos": [
                    {"tipo": "EMAIL", "valor": "juan.perez@example.com"}
                ]
            },
            desc="Crea o sincroniza un donante en el servicio de Incentivos."
        ),
        make_request(
            "Registrar Donación en Incentivos",
            "POST",
            "{{baseUrl_incentivos}}/donacion",
            body_obj={
                "idDonacion": 905,
                "idDonante": 1,
                "fecha": "2026-09-20",
                "CantidadDeBien": 15.0,
                "subcategoria": "Arroz",
                "categoria": "Alimentos",
                "idEntidadBeneficiaria": 1
            },
            desc="Registra una nueva donación para acumular hacia las metas del donante."
        ),
        make_request(
            "Marcar Donación como Recibida",
            "PUT",
            "{{baseUrl_incentivos}}/donacion/905?nombreDeLaEntidad=Comedor+Los+Bajitos",
            desc="Marca la donación como entregada y recibida por la entidad. Dispara la evaluación de misiones y avance de categorías de gamificación."
        ),
        make_request(
            "Consultar Métricas de Actividad del Donante",
            "GET",
            "{{baseUrl_incentivos}}/incentivos/{{donanteId}}/metricas",
            desc="Obtiene estadísticas del donante: cantidad de donaciones realizadas, puntos acumulados y balance de actividad."
        ),
        make_request(
            "Consultar Misiones Completadas",
            "GET",
            "{{baseUrl_incentivos}}/incentivos/{{donanteId}}/misiones",
            desc="Devuelve las misiones que el donante ya ha completado con éxito."
        ),
        make_request(
            "Consultar Insignias del Donante",
            "GET",
            "{{baseUrl_incentivos}}/incentivos/{{donanteId}}/insignias",
            desc="Devuelve el listado de medallas/insignias ganadas por el donante."
        ),
        make_request(
            "Difundir Insignia Obtenida",
            "POST",
            "{{baseUrl_incentivos}}/incentivos-service/difusion/insignia",
            body_obj={
                "idDonante": 1,
                "nombreInsignia": "Donador Estrella",
                "mensaje": "¡Felicitaciones! Has completado la meta mensual de donaciones."
            },
            desc="Envía un evento de difusión al webhook externo configurado (n8n/redes)."
        )
    ]
}

# ==============================================================================
# FOLDER 3: Notificación Service
# ==============================================================================
folder_3 = {
    "name": "03. Notificación Service (Puerto 8082)",
    "item": [
        make_request(
            "Enviar Notificación General",
            "POST",
            "{{baseUrl_notificaciones}}/notificaciones/enviar",
            body_obj={
                "idDestinatarioOrigen": 1,
                "tipoDestinatario": "DONANTE",
                "nombre": "Juan Pérez",
                "mensaje": "¡Gracias por colaborar con DonaTrack! Tu aporte hace la diferencia.",
                "contactos": [
                    {"tipo": "EMAIL", "valor": "juan.perez@example.com"}
                ]
            },
            desc="Envía un mensaje personalizado a través de los canales de contacto del destinatario (Twilio / Email / WhatsApp)."
        ),
        make_request(
            "Notificar por Inactividad",
            "POST",
            "{{baseUrl_notificaciones}}/notificaciones/inactividad",
            body_obj={
                "idDestinatarioOrigen": 1,
                "tipoDestinatario": "DONANTE",
                "nombre": "Juan Pérez",
                "contactos": [
                    {"tipo": "EMAIL", "valor": "juan.perez@example.com"}
                ]
            },
            desc="Dispara la plantilla de reactivación/inactividad para donantes que llevan tiempo sin colaborar."
        ),
        make_request(
            "Notificar Asignación a Entidad Beneficiaria",
            "POST",
            "{{baseUrl_notificaciones}}/notificaciones/asignacion-entidad",
            body_obj={
                "idDestinatarioOrigen": 1,
                "nombre": "Comedor Los Bajitos",
                "detalleDonacion": "50 KG de Arroz y 30 Sillas de madera",
                "contactos": [
                    {"tipo": "EMAIL", "valor": "losbajitos@gmail.com"}
                ]
            },
            desc="Avisa a la entidad que una nueva donación ha sido asignada a sus necesidades."
        ),
        make_request(
            "Notificar Asignación a Donante",
            "POST",
            "{{baseUrl_notificaciones}}/notificaciones/asignacion-donante",
            body_obj={
                "idDestinatarioOrigen": 1,
                "tipoDestinatario": "DONANTE",
                "nombre": "Juan Pérez",
                "detalleDonacion": "50 KG de Arroz",
                "nombreEntidad": "Comedor Los Bajitos",
                "contactos": [
                    {"tipo": "EMAIL", "valor": "juan.perez@example.com"}
                ]
            },
            desc="Avisa al donante a qué entidad específica beneficiará su donación solidaria."
        )
    ]
}

# ==============================================================================
# FOLDER 4: Logística Service
# ==============================================================================
folder_4 = {
    "name": "04. Logística Service (Puerto 8083)",
    "item": [
        {
            "name": "Camiones y Flota",
            "item": [
                make_request(
                    "Listar Todos los Camiones",
                    "GET",
                    "{{baseUrl_logistica}}/logistica-service/camiones",
                    desc="Lista la flota de camiones disponibles para la distribución."
                ),
                make_request(
                    "Agregar Nuevo Camión",
                    "POST",
                    "{{baseUrl_logistica}}/logistica-service/camiones",
                    body_obj={
                        "patente": "AF-987-ZZ",
                        "capacidadEnKg": 4500.0,
                        "volumen": 18.0,
                        "altura": 3.0,
                        "nombreChofer": "Mario Rossi",
                        "direccion": "Av. Rivadavia 5000, CABA",
                        "latitud": -34.6185,
                        "longitud": -58.4350
                    },
                    desc="Registra un nuevo vehículo con su capacidad de carga en kg y volumen m3.",
                    test_script=[
                        "pm.test('Camión registrado', function () {",
                        "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                        "});",
                        "var jsonData = pm.response.json();",
                        "if (jsonData.id) {",
                        "    pm.collectionVariables.set('camionId', jsonData.id);",
                        "}"
                    ]
                ),
                make_request(
                    "Obtener Camión por ID",
                    "GET",
                    "{{baseUrl_logistica}}/logistica-service/camiones/{{camionId}}",
                    desc="Obtiene las características técnicas y chofer asignado de un camión."
                ),
                make_request(
                    "Modificar Chofer del Camión",
                    "PATCH",
                    "{{baseUrl_logistica}}/logistica-service/camiones/{{camionId}}/chofer",
                    headers=[{"key": "Content-Type", "value": "text/plain"}],
                    raw_body="Roberto Sanchez",
                    desc="Actualiza el conductor asignado a un camión."
                )
            ]
        },
        {
            "name": "Recepción y Pesaje de Donaciones",
            "item": [
                make_request(
                    "Recibir Asignación de Donación",
                    "POST",
                    "{{baseUrl_logistica}}/logistica-service/recepcion/asignacion",
                    body_obj={
                        "donacionId": 105,
                        "donanteId": 1,
                        "subcategoria": "Arroz Blanco",
                        "cantidad": 50.0,
                        "fechaVencimiento": "2027-06-30",
                        "entidadId": 1,
                        "razonSocialEntidad": "Comedor Los Bajitos",
                        "direccionEntidad": "Av. Siempreviva 742, CABA",
                        "latitudDestino": -34.598414,
                        "longitudDestino": -58.420215
                    },
                    desc="Endpoint consumido tras el match para ingresar la donación a la cola de logística."
                ),
                make_request(
                    "Cargar Pesaje de Donación",
                    "POST",
                    "{{baseUrl_logistica}}/logistica-service/recepcion/pesaje-donacion/103",
                    body_obj={
                        "peso": 25.5,
                        "volumen": 0.08
                    },
                    desc="Registra el pesaje y cubicaje real de una donación que estaba PENDIENTE_DE_PESAJE. La habilita a LISTA_PARA_PLANIFICAR."
                )
            ]
        },
        {
            "name": "Rutas y Planificación",
            "item": [
                make_request(
                    "Confirmar Planificación de Rutas (Simulación API Rutas)",
                    "POST",
                    "{{baseUrl_logistica}}/logistica-service/rutas/confirmar",
                    body_obj={
                        "event_id": "evt-postman-01",
                        "event_type": "routing-completed",
                        "request_id": "req-seed-01",
                        "timestamp": "2026-09-20T19:00:00",
                        "data": {
                            "routes": [
                                {
                                    "truckId": "1",
                                    "assignedRouteId": "ruta-caba-01",
                                    "estimatedStartTime": "2026-09-21T08:30:00",
                                    "estimatedEndTime": "2026-09-21T13:00:00",
                                    "totalDistanceKm": 28.4,
                                    "totalDurationMins": 140,
                                    "stops": [
                                        {
                                            "stopNumber": 1,
                                            "deliveryCode": "DEL-LOSPIBES-01",
                                            "estimatedArrivalTime": "2026-09-21T09:30:00"
                                        }
                                    ]
                                }
                            ],
                            "unassignedDeliveries": []
                        }
                    },
                    desc="Simula la respuesta del microservicio externo de ruteo para consolidar la ruta y sus paradas.",
                    test_script=[
                        "pm.test('Rutas procesadas exitosamente', function () {",
                        "    pm.response.to.have.status(200);",
                        "});"
                    ]
                ),
                make_request(
                    "Listar Rutas Generadas",
                    "GET",
                    "{{baseUrl_logistica}}/logistica-service/rutas",
                    desc="Obtiene las rutas creadas con sus paradas, camión asignado y estado.",
                    test_script=[
                        "var jsonData = pm.response.json();",
                        "if (Array.isArray(jsonData) && jsonData.length > 0) {",
                        "    pm.collectionVariables.set('rutaId', jsonData[0].id);",
                        "}"
                    ]
                ),
                make_request(
                    "Obtener Ruta por ID",
                    "GET",
                    "{{baseUrl_logistica}}/logistica-service/rutas/{{rutaId}}",
                    desc="Obtiene los datos detallados de una ruta específica."
                ),
                make_request(
                    "Iniciar Ruta de Entrega",
                    "PATCH",
                    "{{baseUrl_logistica}}/logistica-service/rutas/{{rutaId}}/iniciar-ruta",
                    headers=[{"key": "Content-Type", "value": "text/plain"}],
                    raw_body="Mario Rossi",
                    desc="Cambia el estado de la ruta a VIAJE_COMENZADO y publica el evento 'inicio.ruta' en RabbitMQ.",
                    test_script=[
                        "pm.test('Ruta iniciada', function () {",
                        "    pm.response.to.have.status(200);",
                        "    pm.expect(pm.response.json().estado).to.eql('VIAJE_COMENZADO');",
                        "});"
                    ]
                )
            ]
        },
        {
            "name": "Gestión de Entregas",
            "item": [
                make_request(
                    "Listar Entregas Pendientes",
                    "GET",
                    "{{baseUrl_logistica}}/logistica-service/entregas/pendientes",
                    desc="Obtiene las entregas programadas que aún no han sido completadas.",
                    test_script=[
                        "var jsonData = pm.response.json();",
                        "if (Array.isArray(jsonData) && jsonData.length > 0) {",
                        "    pm.collectionVariables.set('entregaId', jsonData[0].id);",
                        "}"
                    ]
                ),
                make_request(
                    "Obtener Entrega por ID",
                    "GET",
                    "{{baseUrl_logistica}}/logistica-service/entregas/{{entregaId}}",
                    desc="Detalle de una entrega con sus donaciones y entidad receptora."
                ),
                make_request(
                    "Marcar Entrega como Completada",
                    "PATCH",
                    "{{baseUrl_logistica}}/logistica-service/entregas/{{entregaId}}/estado",
                    body_obj={
                        "nuevoEstado": "COMPLETADA",
                        "observaciones": "Entrega concretada con éxito en la sede del comedor.",
                        "fecha": "2026-09-20"
                    },
                    desc="Finaliza la entrega y emite el evento 'entrega.exito' en RabbitMQ, lo que hace que Donaciones marque la donación como ENTREGADA.",
                    test_script=[
                        "pm.test('Entrega completada', function () {",
                        "    pm.response.to.have.status(200);",
                        "});"
                    ]
                ),
                make_request(
                    "Marcar Entrega como No Satisfactoria (Fallida)",
                    "PATCH",
                    "{{baseUrl_logistica}}/logistica-service/entregas/{{entregaId}}/estado",
                    body_obj={
                        "nuevoEstado": "FALLIDA",
                        "observaciones": "La entidad se encontraba cerrada en el horario de visita.",
                        "fecha": "2026-09-20"
                    },
                    desc="Registra el fallo de entrega y publica 'entrega.no.satisfactoria' para re-planificación."
                )
            ]
        }
    ]
}

# ==============================================================================
# FOLDER 5: Flujo E2E Completo
# ==============================================================================
folder_5 = {
    "name": "05. Flujo E2E Completo (Paso a Paso)",
    "description": "Secuencia ordenada que orquesta todo el ciclo de vida del sistema DonaTrack de punta a punta: Registro de donante y entidad -> Creación de donación -> Matchmaking automático -> Notificaciones -> Logística y Ruteo -> Completitud e Incentivos.",
    "item": [
        make_request(
            "Paso 1: [Donaciones] Crear Donante Humano",
            "POST",
            "{{baseUrl_donaciones}}/donaciones-service/donantes",
            body_obj={
                "tipo": "HUMANA",
                "nombre": "Valeria",
                "apellido": "Sosa",
                "fechaDeNacimiento": "1994-08-14",
                "genero": "Femenino",
                "numeroDeDocumento": "38456123",
                "contactos": [
                    {"tipo": "EMAIL", "valor": "valeria.sosa@example.com"},
                    {"tipo": "WHATSAPP", "valor": "+5491177665544"}
                ]
            },
            desc="Crea un donante en Donaciones. El servicio automáticamente sincroniza el nuevo donante con Incentivos.",
            test_script=[
                "pm.test('Donante creado exitosamente', function () {",
                "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                "});",
                "var data = pm.response.json();",
                "pm.collectionVariables.set('donanteId', data.id);",
                "console.log('Nuevo donante ID:', data.id);"
            ]
        ),
        make_request(
            "Paso 2: [Donaciones] Crear Entidad Beneficiaria",
            "POST",
            "{{baseUrl_donaciones}}/donaciones-service/entidades-beneficiarias",
            body_obj={
                "razonSocial": "Hogar Luz y Vida",
                "tipoEntidad": "HOGAR",
                "calle": "Av. Corrientes",
                "altura": 3200,
                "contactos": [
                    {"tipo": "EMAIL", "valor": "luzyvida@gmail.com"},
                    {"tipo": "WHATSAPP", "valor": "+5491133221100"}
                ],
                "representantes": []
            },
            desc="Registra una nueva entidad en el sistema.",
            test_script=[
                "pm.test('Entidad creada exitosamente', function () {",
                "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                "});",
                "var data = pm.response.json();",
                "pm.collectionVariables.set('entidadId', data.id);",
                "console.log('Nueva entidad ID:', data.id);"
            ]
        ),
        make_request(
            "Paso 3: [Donaciones] Cargar Necesidad a la Entidad",
            "POST",
            "{{baseUrl_donaciones}}/donaciones-service/entidades-beneficiarias/{{entidadId}}/necesidades",
            body_obj={
                "subcategoriaId": 1,
                "subcategoria": "Arroz",
                "cantidad": 25.0,
                "unidadDeMedida": "KG",
                "descripcion": "Necesidad urgente de arroz para raciones semanales",
                "tipoNecesidad": "EXTRAORDINARIA",
                "periodo": None,
                "inicioRecurrencia": None
            },
            desc="Publica la necesidad de la entidad para el matcheador.",
            test_script=[
                "pm.test('Necesidad registrada', function () {",
                "    pm.expect(pm.response.code).to.be.oneOf([200, 201, 204]);",
                "});"
            ]
        ),
        make_request(
            "Paso 4: [Donaciones] Donante Realiza una Donación",
            "POST",
            "{{baseUrl_donaciones}}/donaciones-service/donaciones",
            body_obj={
                "idDonante": 1,
                "descripcionGeneral": "Caja con 25 kg de arroz de primera marca",
                "bienes": [
                    {
                        "nombre": "Arroz Doble Carolina",
                        "descripcion": "Paquetes de 1kg cerrados",
                        "cantidad": 25.0,
                        "unidadDeMedida": "KG",
                        "subcategoriaId": 1,
                        "estado": "NUEVO",
                        "fechaVencimiento": "2027-10-15",
                        "urlFoto": "https://example.com/arroz-carolina.jpg"
                    }
                ]
            },
            desc="Crea la donación en estado DEPOSITO y la notifica a Incentivos.",
            test_script=[
                "pm.test('Donación registrada en depósito', function () {",
                "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                "});",
                "var data = pm.response.json();",
                "var idDonacion = Array.isArray(data) ? data[0].id : data.id;",
                "pm.collectionVariables.set('donacionId', idDonacion);",
                "console.log('Nueva donación ID:', idDonacion);"
            ]
        ),
        make_request(
            "Paso 5: [Donaciones] Consultar Matcheo de la Donación",
            "GET",
            "{{baseUrl_donaciones}}/donaciones-service/donaciones/matcheador",
            desc="Obtiene las sugerencias generadas. Guarda los IDs de matcheo y de la necesidad sugerida.",
            test_script=[
                "pm.test('Sugerencias de matcheo obtenidas', function () {",
                "    pm.response.to.have.status(200);",
                "});",
                "var list = pm.response.json();",
                "pm.expect(list.length).to.be.greaterThan(0);",
                "pm.collectionVariables.set('matcheoId', list[0].id);",
                "if (list[0].necesidadesSugeridas && list[0].necesidadesSugeridas.length > 0) {",
                "    pm.collectionVariables.set('necesidadId', list[0].necesidadesSugeridas[0].id);",
                "}"
            ]
        ),
        make_request(
            "Paso 6: [Donaciones] Administrador Confirma el Match",
            "POST",
            "{{baseUrl_donaciones}}/donaciones-service/donaciones/matcheador/{{matcheoId}}/{{necesidadId}}",
            body_obj={"autorDelCambio": "Admin General"},
            desc="Confirma la asignación de la donación a la necesidad. Comunica automáticamente a Logística (recepción de donación), Incentivos (actualización de donación) y Notificaciones (al donante y a la entidad).",
            test_script=[
                "pm.test('Asignación confirmada por administrador', function () {",
                "    pm.expect(pm.response.code).to.be.oneOf([200, 204]);",
                "});"
            ]
        ),
        make_request(
            "Paso 7: [Logística] Cargar Pesaje Real de la Donación",
            "POST",
            "{{baseUrl_logistica}}/logistica-service/recepcion/pesaje-donacion/{{donacionId}}",
            body_obj={
                "peso": 26.2,
                "volumen": 0.075
            },
            desc="En el depósito de logística se pesa y mide la mercadería. Pasa su estado a LISTA_PARA_PLANIFICAR.",
            test_script=[
                "pm.test('Pesaje registrado con éxito', function () {",
                "    pm.expect(pm.response.code).to.be.oneOf([200, 201]);",
                "});"
            ]
        ),
        make_request(
            "Paso 8: [Logística] Confirmar Hoja de Ruta del Camión",
            "POST",
            "{{baseUrl_logistica}}/logistica-service/rutas/confirmar",
            body_obj={
                "event_id": "evt-flow-001",
                "event_type": "routing-completed",
                "request_id": "req-flow-001",
                "timestamp": "2026-09-20T19:30:00",
                "data": {
                    "routes": [
                        {
                            "truckId": "1",
                            "assignedRouteId": "ruta-e2e-01",
                            "estimatedStartTime": "2026-09-21T08:00:00",
                            "estimatedEndTime": "2026-09-21T12:00:00",
                            "totalDistanceKm": 25.0,
                            "totalDurationMins": 120,
                            "stops": [
                                {
                                    "stopNumber": 1,
                                    "deliveryCode": "DEL-HOGAR-01",
                                    "estimatedArrivalTime": "2026-09-21T09:15:00"
                                }
                            ]
                        }
                    ],
                    "unassignedDeliveries": []
                }
            },
            desc="Genera la ruta asignada al camión con sus paradas de entrega.",
            test_script=[
                "pm.test('Ruta consolidada', function () {",
                "    pm.response.to.have.status(200);",
                "});"
            ]
        ),
        make_request(
            "Paso 9: [Logística] Iniciar Ruta de Distribución",
            "PATCH",
            "{{baseUrl_logistica}}/logistica-service/rutas/1/iniciar-ruta",
            headers=[{"key": "Content-Type", "value": "text/plain"}],
            raw_body="Mario Rossi",
            desc="El chofer inicia el recorrido. Se emite el evento 'inicio.ruta' por RabbitMQ hacia Donaciones.",
            test_script=[
                "pm.test('Ruta en viaje', function () {",
                "    pm.response.to.have.status(200);",
                "});"
            ]
        ),
        make_request(
            "Paso 10: [Logística] Marcar Entrega como Exitosa",
            "PATCH",
            "{{baseUrl_logistica}}/logistica-service/entregas/1/estado",
            body_obj={
                "nuevoEstado": "COMPLETADA",
                "observaciones": "Donación entregada en mano a los directores del hogar.",
                "fecha": "2026-09-21"
            },
            desc="Se concreta la entrega en destino. Logística publica 'entrega.exito' en RabbitMQ, lo que hace que Donaciones cambie el estado a ENTREGADA.",
            test_script=[
                "pm.test('Entrega completada', function () {",
                "    pm.response.to.have.status(200);",
                "});"
            ]
        ),
        make_request(
            "Paso 11: [Incentivos] Consultar Métricas y Puntos del Donante",
            "GET",
            "{{baseUrl_incentivos}}/incentivos/{{donanteId}}/metricas",
            desc="Verifica que el donante haya sumado puntos, actividad y progreso de misiones tras la donación concretada.",
            test_script=[
                "pm.test('Métricas obtenidas con éxito', function () {",
                "    pm.response.to.have.status(200);",
                "});"
            ]
        ),
        make_request(
            "Paso 12: [Notificaciones] Enviar Notificación de Agradecimiento Final",
            "POST",
            "{{baseUrl_notificaciones}}/notificaciones/enviar",
            body_obj={
                "idDestinatarioOrigen": 1,
                "tipoDestinatario": "DONANTE",
                "nombre": "Valeria Sosa",
                "mensaje": "¡Tu donación de 25 KG de Arroz ha llegado al Hogar Luz y Vida! Gracias por sumar tu granito de arena.",
                "contactos": [
                    {"tipo": "EMAIL", "valor": "valeria.sosa@example.com"}
                ]
            },
            desc="Envía la notificación final al donante informando el éxito de la entrega.",
            test_script=[
                "pm.test('Notificación enviada', function () {",
                "    pm.response.to.have.status(200);",
                "});"
            ]
        )
    ]
}

collection["item"].append(folder_0)
collection["item"].append(folder_1)
collection["item"].append(folder_2)
collection["item"].append(folder_3)
collection["item"].append(folder_4)
collection["item"].append(folder_5)

output_dir = "postman"
os.makedirs(output_dir, exist_ok=True)
output_path = os.path.join(output_dir, "DonaTrack_E2E_Completo.postman_collection.json")

with open(output_path, "w", encoding="utf-8") as f:
    json.dump(collection, f, indent=2, ensure_ascii=False)

print(f"Postman collection successfully generated at: {output_path}")
