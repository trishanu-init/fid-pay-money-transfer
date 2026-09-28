import { Component } from '@angular/core';

/**
 * Root Application Component
 * 
 * Contains only the router outlet for feature module components.
 */
@Component({
  selector: 'app-root',
  template: '<router-outlet></router-outlet>',
  styles: []
})
export class AppComponent {
  title = 'Money Transfer System';
}
