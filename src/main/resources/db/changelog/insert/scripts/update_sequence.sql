DO $$
    DECLARE
        table_record RECORD;
        sequence_name TEXT;
    BEGIN
        FOR table_record IN (
            SELECT
                c.relname AS table_name,
                a.attname AS column_name,
                pg_get_serial_sequence(n.nspname || '.' || c.relname, a.attname) AS seq_name
            FROM
                pg_class c
                    JOIN pg_namespace n ON n.oid = c.relnamespace
                    JOIN pg_attribute a ON a.attrelid = c.oid
            WHERE
                n.nspname = 'public'
              AND c.relkind = 'r'
              AND a.attnum > 0
              AND NOT a.attisdropped
              AND pg_get_serial_sequence(n.nspname || '.' || c.relname, a.attname) IS NOT NULL
        ) LOOP
                IF table_record.seq_name IS NOT NULL THEN
                    sequence_name := table_record.seq_name;
                    EXECUTE format(
                            'SELECT setval(%L, (SELECT COALESCE(MAX(%I), 1) FROM %I.%I))',
                            sequence_name,
                            table_record.column_name,
                            'public',
                            table_record.table_name
                            );
                    RAISE NOTICE 'Updated sequence % for table %.%', sequence_name, 'public', table_record.table_name;
                ELSE
                    RAISE NOTICE 'No sequence found for table %.%', 'public', table_record.table_name;
                END IF;
            END LOOP;
    END $$;