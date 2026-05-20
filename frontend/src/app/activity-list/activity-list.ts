/**
 * activity-list.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Standalone Angular 21 component rendering employee time-tracking data
 *              fetched from the Spring ActivityRegistryController.
 *
 *              Technology choices:
 *                • httpResource  — reactive HTTP, auto-cancels on param change
 *                • linkedSignal  — keeps the local dropdown in sync with the service signal
 *                • computed      — derives typed sub-arrays and guards hasValue() this-binding
 *                • @if / @for / @switch — built-in Angular 21 control flow, no *ngIf/*ngFor
 *                • Fully zoneless-ready (no NgZone dependency)
 *
 *              ⚠ hasValue() is a plain method on the resource, NOT a Signal.
 *                Aliasing it directly (readonly hasValue = resource.hasValue) strips the
 *                `this` context and causes "isValueDefined is not a function" at runtime.
 *                The fix is to wrap it in computed(() => resource.hasValue()) so Angular's
 *                reactive graph tracks it correctly without touching `this`.
 */

import { Component, computed, inject, linkedSignal } from '@angular/core';

import { ActivityService }         from '../service/activity-service';
import { GroupStrategy }           from '../model/group-strategy.model';
import { ActivityRegistry }        from '../model/activity-registry.model';
import { ProjectHoursRow }         from '../model/project-hours-row.model';
import { ProjectEmployeeHoursRow } from '../model/project-employee-hours-row.model';
import { EmployeeProjectHoursRow } from '../model/employee-project-hours-row.model';

@Component({
  selector:    'app-activity-list',
  standalone:  true,
  templateUrl: './activity-list.html',
  styleUrl:    './activity-list.css',
})
export class ActivityList {

  // ── Dependency injection ─────────────────────────────────────────────────
  protected readonly svc = inject(ActivityService);

  /**
   * linkedSignal mirrors the service's groupBy.
   * Reading it in the template gives a local writable signal for the <select>.
   * Writing to it (via `onStrategyChange`) pushes back to the service,
   * which in turn triggers a new httpResource request automatically.
   */
  protected readonly selectedStrategy = linkedSignal<GroupStrategy>(
    () => this.svc.groupBy()
  );

  // ── Convenience aliases from the resource ────────────────────────────────
  // IMPORTANT: hasValue() is a plain method, not a Signal. Assigning it directly
  //   (e.g. readonly hasValue = resource.hasValue) detaches it from its instance,
  //   breaking the internal call to `this.isValueDefined()` at runtime.
  //   Wrapping in computed() keeps `this` bound correctly AND makes the value
  //   trackable by Angular's reactive graph so the template re-renders on change.

  /** True while the HTTP request is in flight. */
  protected readonly isLoading = computed(() => this.svc.activitiesResource.isLoading());

  /** Populated only when the resource is in an error state. */
  protected readonly error = computed(() => this.svc.activitiesResource.error());

  /**
   * True when valid data is available.
   * Guards against reading value() while the resource is in an error or loading state.
   */
  protected readonly hasValue = computed(() => this.svc.activitiesResource.hasValue());

  // ── Typed computed projections ───────────────────────────────────────────
  // Each computed reads groupBy() + hasValue() so it re-evaluates reactively.

  protected readonly rawActivities = computed<ActivityRegistry[]>(() =>
    this.hasValue() && this.svc.groupBy() === 'NONE'
      ? (this.svc.activitiesResource.value() as ActivityRegistry[])
      : []
  );

  protected readonly projectRows = computed<ProjectHoursRow[]>(() =>
    this.hasValue() && this.svc.groupBy() === 'PROJECT'
      ? (this.svc.activitiesResource.value() as ProjectHoursRow[])
      : []
  );

  protected readonly projectEmployeeRows = computed<ProjectEmployeeHoursRow[]>(() =>
    this.hasValue() && this.svc.groupBy() === 'PROJECT_EMPLOYEE'
      ? (this.svc.activitiesResource.value() as ProjectEmployeeHoursRow[])
      : []
  );

  protected readonly employeeProjectRows = computed<EmployeeProjectHoursRow[]>(() =>
    this.hasValue() && this.svc.groupBy() === 'EMPLOYEE_PROJECT'
      ? (this.svc.activitiesResource.value() as EmployeeProjectHoursRow[])
      : []
  );

  /** Total hours across all rows — shown in the footer for non-NONE strategies. */
  protected readonly totalHours = computed<number>(() => {
    if (!this.hasValue()) return 0;
    const rows = this.svc.activitiesResource.value() ?? [];
    return rows.reduce((sum, r) => sum + (('totalHours' in r) ? r.totalHours : (r as ActivityRegistry).hours), 0);
  });

  // ── Event handlers ───────────────────────────────────────────────────────

  /**
   * Called by the template <select> on change.
   * Updates the linkedSignal (local) and the service signal (shared),
   * which automatically triggers a new httpResource fetch.
   */
  protected onStrategyChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value as GroupStrategy;
    this.selectedStrategy.set(value);
    this.svc.setGroupBy(value);
  }

  /** Triggers a manual reload of the resource (e.g. refresh button). */
  protected reload(): void {
    this.svc.activitiesResource.reload();
  }
}