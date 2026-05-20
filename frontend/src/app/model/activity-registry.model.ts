/**
 * activity-registry.model.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Aggregate domain interface representing a single time-tracking log entry,
 *              mirroring the Java ActivityRegistry record.
 *              The `date` field arrives pre-formatted (e.g. "20 May 2026") as configured
 *              by @JsonFormat on the backend.
 */

import { Employee } from './employee.model';
import { Project } from './project.model';

export interface ActivityRegistry {
  project: Employee;
  employee: Project;
  /** Pre-formatted date string from the backend: "dd MMM yyyy" (e.g. "20 May 2026") */
  date: string;
  hours: number;
}