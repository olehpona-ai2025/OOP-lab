CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    row_id TEXT NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL,
    changed_by TEXT,
    old_data JSONB,
    new_data JSONB
);

CREATE INDEX idx_audit_log_table_row 
ON audit_log (table_name, row_id);

CREATE INDEX idx_audit_log_changed_at 
ON audit_log (changed_at);

CREATE INDEX idx_audit_log_changed_by 
ON audit_log (changed_by);

create or replace function audit_row_change()
returns trigger
language plpgsql
as $$
declare
  actor text;
begin
  actor := coalesce(
    nullif(current_setting('app.actor', true), ''),
    current_user
  );

  if TG_OP = 'INSERT' then
    insert into audit_log(table_name, row_id, operation, changed_at, changed_by, old_data, new_data)
    values (TG_TABLE_NAME, (NEW.id)::text, 'INSERT', now(), actor, null, to_jsonb(NEW));
    return NEW;
  elsif TG_OP = 'UPDATE' then
    insert into audit_log(table_name, row_id, operation, changed_at, changed_by, old_data, new_data)
    values (TG_TABLE_NAME, (NEW.id)::text, 'UPDATE', now(), actor, to_jsonb(OLD), to_jsonb(NEW));
    return NEW;
  elsif TG_OP = 'DELETE' then
    insert into audit_log(table_name, row_id, operation, changed_at, changed_by, old_data, new_data)
    values (TG_TABLE_NAME, (OLD.id)::text, 'DELETE', now(), actor, to_jsonb(OLD), null);
    return OLD;
  end if;

  return null;
end;
$$;

create trigger trg_audit_farm_area
after insert or update or delete on farm_area_entity
for each row execute function audit_row_change();

create trigger trg_audit_warehouse
after insert or update or delete on warehouse_entity
for each row execute function audit_row_change();

create trigger trg_audit_workers
after insert or update or delete on worker_entity
for each row execute function audit_row_change();