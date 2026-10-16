package com.sahayakai.api.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health/database")
public class DatabaseHealthController {

    private final DatabaseHealthService databaseHealthService;

    public DatabaseHealthController(DatabaseHealthService databaseHealthService) {
        this.databaseHealthService = databaseHealthService;
    }

    @GetMapping
    public ResponseEntity<DatabaseHealthResponse> checkDatabase() {
        DatabaseHealthResponse response = databaseHealthService.checkConnection();
        HttpStatus status = response.connected() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(response);
    }
}
