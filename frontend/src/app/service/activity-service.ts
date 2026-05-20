/**
 * activity-service.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Signal-first service encapsulating the ActivityRegistry data layer.
 *              Owns the reactive groupBy selector and the httpResource that drives
 *              all backend calls — no Observables, no manual subscriptions.
 *
 *              httpResource auto-cancels in-flight requests whenever groupBy changes,
 *              guaranteeing race-condition-free data loading out of the box.
 *
 *              API versioning: the Spring controller exposes two versions of the
 *              same path ("/api/activities") dispatched via the X-API-Version header:
 *                • X-API-Version: 2.0  →  groupBy-aware endpoint  (used here)
 *                • X-API-Version: 1.0  →  raw list endpoint (not used — v2 with NONE is equivalent)
 */

import { Injectable, signal } from '@angular/core';
import { httpResource } from '@angular/common/http';

import { GroupStrategy, GROUP_STRATEGY_OPTIONS } from '../model/group-strategy.model';
import { ActivityRegistry }             from '../model/activity-registry.model';
import { ProjectHoursRow }              from '../model/project-hours-row.model';
import { ProjectEmployeeHoursRow }      from '../model/project-employee-hours-row.model';
import { EmployeeProjectHoursRow }      from '../model/employee-project-hours-row.model';

/** Union of all possible backend response row shapes. */
export type ActivityRow =
  | ActivityRegistry
  | ProjectHoursRow
  | ProjectEmployeeHoursRow
  | EmployeeProjectHoursRow;

/** Base URL of the Spring REST controller. */
const API_BASE = 'http://localhost:8080/api/activities';

@Injectable({ providedIn: 'root' })
export class ActivityService {

  /** Exposed so components can enumerate the dropdown options. */
  readonly strategyOptions = GROUP_STRATEGY_OPTIONS;

  /**
   * Writable signal holding the currently selected aggregation strategy.
   * Changing it automatically triggers a new HTTP request via `activitiesResource`.
   */
  readonly groupBy = signal<GroupStrategy>('NONE');

  /**
   * Reactive HTTP resource — the single data source for the entire feature.
   *
   * The arrow function is re-evaluated every time `groupBy` changes:
   *   • httpResource cancels any pending request and issues a fresh one.
   *   • value(), isLoading(), error() and hasValue() expose the current state as signals.
   *
   * Typed as `ActivityRow[]` (union); the consuming component narrows to the concrete
   * sub-type via `computed` after reading `groupBy()`.
   */
  readonly activitiesResource = httpResource<ActivityRow[]>(() => ({
    url: API_BASE,
    params: { groupBy: this.groupBy() },
    // Route to the versioned endpoint — the Spring @GetMapping(version="2.0") dispatcher
    // matches on this header and exposes the groupBy-aware projection logic.
    headers: { 'X-API-Version': '2.0' },
  }));

  /** Convenience setter — keeps the mutation API surface minimal. */
  setGroupBy(strategy: GroupStrategy): void {
    this.groupBy.set(strategy);
  }
}