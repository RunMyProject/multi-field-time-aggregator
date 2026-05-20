
import { Component, signal } from '@angular/core';
import { ActivityList} from './activity-list/activity-list';

/**
 * app.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Provides the main application component, which serves as the root of the Angular application.
 * The `App` component is responsible for rendering the main structure of the application and can include
 * other components, such as the `ActivityList`, to display specific content. The `title` signal is used
 * to manage reactive state within the component, allowing for dynamic updates to the UI when the state changes.  
*/
@Component({
  selector: 'app-root',
  imports: [ActivityList],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');
}
