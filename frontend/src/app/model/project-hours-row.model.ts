/**
 * project-hours-row.model.ts
 * Author: Edoardo Sabatini
 * Date: 2026-05-20
 * Description: Projection DTO representing aggregated hours bucketed by project,
 *              mirroring the Java ProjectHoursRow record.
 *              Returned by the backend when groupBy=PROJECT.
 */

export interface ProjectHoursRow {
  projectName: string;
  totalHours: number;
}