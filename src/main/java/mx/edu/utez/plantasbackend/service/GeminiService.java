package mx.edu.utez.plantasbackend.service;

import mx.edu.utez.plantasbackend.model.Planta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-3.8-flash}")
    private String model;

    private final RestTemplate restTemplate = createRestTemplate();

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(45000);
        return new RestTemplate(factory);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Planta analizarPlanta(String base64Image, String mimeType) throws Exception {
        String prompt = "Analiza la imagen de esta planta e identificala con precision. " +
                "Responde UNICAMENTE con un objeto JSON valido (sin bloques markdown) con exactamente estas claves: " +
                "{" +
                "\"nombreComun\": \"Nombre comun en espanol\", " +
                "\"nombreCientifico\": \"Nombre cientifico en latin\", " +
                "\"categoria\": \"Planta de interior o Planta de exterior\", " +
                "\"descripcion\": \"Descripcion breve y clara de la planta (2 a 3 oraciones)\", " +
                "\"caracteristicasPrincipales\": \"Caracteristicas fisicas y botanicas destacadas\", " +
                "\"frecuenciaRiegoDias\": 7, " +
                "\"frecuenciaRiegoTexto\": \"Ej: Cada 7 dias\", " +
                "\"cantidadAgua\": \"Cantidad o forma recomendada de riego\", " +
                "\"necesidadLuz\": \"Ej: Luz indirecta brillante\", " +
                "\"tipoIluminacion\": \"Sol directo, Semisombra o Sombra\", " +
                "\"temperatura\": \"Rango ideal en C, ej: 18C - 28C\", " +
                "\"humedad\": \"Nivel de humedad recomendado\", " +
                "\"tipoSuelo\": \"Tipo de sustrato recomendado\", " +
                "\"recomendacionesGenerales\": \"Consejos practicos de poda, abono y prevencion de plagas\"" +
                "}";

        Map inlineData = new HashMap();
        inlineData.put("mime_type", mimeType != null ? mimeType : "image/jpeg");
        inlineData.put("data", base64Image);

        Map imagePart = new HashMap();
        imagePart.put("inline_data", inlineData);

        Map textPart = new HashMap();
        textPart.put("text", prompt);

        Map content = new HashMap();
        content.put("parts", List.of(textPart, imagePart));

        Map generationConfig = new HashMap();
        generationConfig.put("response_mime_type", "application/json");

        Map requestBody = new HashMap();
        requestBody.put("contents", List.of(content));
        requestBody.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(requestBody, headers);

        // Lista de modelos: intenta primero el principal y si hay saturación (503) usa respaldo
        List<String> modelosAProbar = List.of(model, "gemini-3.5-flash-lite", "gemini-3.5-flash");
        ResponseEntity response = null;
        Exception ultimoError = null;

        for (String modeloActual : modelosAProbar) {
            for (int intento = 1; intento <= 2; intento++) {
                try {
                    String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                            + modeloActual + ":generateContent?key=" + apiKey;
                    response = restTemplate.postForEntity(url, entity, Map.class);
                    if (response.getBody() != null) {
                        break;
                    }
                } catch (HttpServerErrorException.ServiceUnavailable e) {
                    ultimoError = e;
                    System.out.println("Modelo " + modeloActual + " saturado (503), intento " + intento + "...");
                    Thread.sleep(1500);
                } catch (Exception e) {
                    ultimoError = e;
                    break;
                }
            }
            if (response != null && response.getBody() != null) {
                break;
            }
        }

        if (response == null || response.getBody() == null) {
            throw ultimoError != null ? ultimoError : new RuntimeException("No se obtuvo respuesta de Gemini");
        }

        Map body = (Map) response.getBody();
        if (!body.containsKey("candidates")) {
            throw new RuntimeException("Respuesta vacia de Gemini");
        }

        List candidates = (List) body.get("candidates");
        Map firstCandidate = (Map) candidates.get(0);
        Map contentMap = (Map) firstCandidate.get("content");
        List parts = (List) contentMap.get("parts");
        Map firstPart = (Map) parts.get(0);
        String jsonText = String.valueOf(firstPart.get("text"));

        jsonText = jsonText.replace("```json", "").replace("```", "").trim();

        ObjectMapper mapper = new ObjectMapper();
        Map plantaMap = mapper.readValue(jsonText, Map.class);

        Planta planta = new Planta();
        planta.setNombreComun(getString(plantaMap, "nombreComun", "Planta desconocida"));
        planta.setNombreCientifico(getString(plantaMap, "nombreCientifico", "No especificado"));
        planta.setCategoria(getString(plantaMap, "categoria", "Planta de interior"));
        planta.setDescripcion(getString(plantaMap, "descripcion", ""));
        planta.setCaracteristicasPrincipales(getString(plantaMap, "caracteristicasPrincipales", ""));
        planta.setFrecuenciaRiegoDias(getInt(plantaMap, "frecuenciaRiegoDias", 7));
        planta.setFrecuenciaRiegoTexto(getString(plantaMap, "frecuenciaRiegoTexto", "Cada 7 dias"));
        planta.setCantidadAgua(getString(plantaMap, "cantidadAgua", "Moderada"));
        planta.setNecesidadLuz(getString(plantaMap, "necesidadLuz", "Indirecta"));
        planta.setTipoIluminacion(getString(plantaMap, "tipoIluminacion", "Semisombra"));
        planta.setTemperatura(getString(plantaMap, "temperatura", "18°C - 26°C"));
        planta.setHumedad(getString(plantaMap, "humedad", "Media"));
        planta.setTipoSuelo(getString(plantaMap, "tipoSuelo", "Sustrato universal drenante"));
        planta.setRecomendacionesGenerales(getString(plantaMap, "recomendacionesGenerales", ""));

        return planta;
    }

    private String getString(Map map, String key, String defaultVal) {
        Object val = map.get(key);
        return val != null ? String.valueOf(val) : defaultVal;
    }

    private Integer getInt(Map map, String key, int defaultVal) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return val != null ? Integer.parseInt(String.valueOf(val)) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}