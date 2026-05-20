import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';

/**
 * app.config.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Provides the application configuration, including global error handling and HTTP client setup.
 * This configuration is essential for ensuring that the application can handle errors gracefully 
 * and communicate with backend APIs effectively.
 * The `provideBrowserGlobalErrorListeners` function sets up a global error handler that captures unhandled errors
 *  in the browser environment, allowing for better debugging and user experience.
 * The `provideHttpClient` function registers the HTTP client service, which is crucial for
 *  making HTTP requests to interact with backend services and APIs.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(), // It manages global error handling in the browser, ensuring that unhandled 
    // errors are captured and can be logged or displayed to the user appropriately.
    provideHttpClient()                  // It manages HTTP client setup for making requests to backend services and APIs.
  ]
};