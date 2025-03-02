#!/bin/sh
echo "Экспорт данных из MinIO в minio_data..."

# Удаляем старые данные
#rm -rf /data/initial/categories/*
#rm -rf /data/initial/subcategories/*
#rm -rf /data/initial/establishments/*
#rm -rf /data/initial/products/*

# Настройка подключения
mc alias set myminio http://minio:9000 minioadmin minioadmin

# Экспортируем изображения из categories
mc find myminio/categories --name "*.png" --exec "mc cp {} /data/initial/categories/"
mc find myminio/categories --name "*.jpg" --exec "mc cp {} /data/initial/categories/"
mc find myminio/categories --name "*.jpeg" --exec "mc cp {} /data/initial/categories/"
mc find myminio/categories --name "*.svg" --exec "mc cp {} /data/initial/categories/"

# Экспортируем изображения из subcategories
mc find myminio/subcategories --name "*.png" --exec "mc cp {} /data/initial/subcategories/"
mc find myminio/subcategories --name "*.jpg" --exec "mc cp {} /data/initial/subcategories/"
mc find myminio/subcategories --name "*.jpeg" --exec "mc cp {} /data/initial/subcategories/"
mc find myminio/subcategories --name "*.svg" --exec "mc cp {} /data/initial/subcategories/"

# Экспортируем изображения из establishments
mc find myminio/establishments --name "*.png" --exec "mc cp {} /data/initial/establishments/"
mc find myminio/establishments --name "*.jpg" --exec "mc cp {} /data/initial/establishments/"
mc find myminio/establishments --name "*.jpeg" --exec "mc cp {} /data/initial/establishments/"
mc find myminio/establishments --name "*.svg" --exec "mc cp {} /data/initial/establishments/"

# Экспортируем изображения из products
mc find myminio/products --name "*.png" --exec "mc cp {} /data/initial/products/"
mc find myminio/products --name "*.jpg" --exec "mc cp {} /data/initial/products/"
mc find myminio/products --name "*.jpeg" --exec "mc cp {} /data/initial/products/"
mc find myminio/products --name "*.svg" --exec "mc cp {} /data/initial/products/"

echo "Экспорт данных завершён. Данные сохранены в minio_data."
