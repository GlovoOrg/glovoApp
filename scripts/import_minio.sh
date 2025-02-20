# Импорт данных в бакеты MinIO
for BUCKET in "${BUCKETS[@]}"; do
  mc mb myminio/$BUCKET --ignore-existing

  # Копирование данных из minio_data в runtime бакеты
  if [ -d "/data/initial/$BUCKET" ] && [ "$(ls -A /data/initial/$BUCKET)" ]; then
    echo "Импорт данных в $BUCKET..."
    mc cp --recursive /data/initial/$BUCKET/ myminio/$BUCKET/
  else
    echo "Нет данных для импорта в $BUCKET, пропуск."
  fi
done