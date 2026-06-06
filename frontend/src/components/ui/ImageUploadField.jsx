import React, { useRef, useCallback, useState } from 'react';
import { Upload, X, ImageIcon } from 'lucide-react';

const MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
const ACCEPTED_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

/**
 * Drag & drop / file input for product image upload.
 * Manages preview locally; parent receives File via onFileChange.
 *
 * @param {Object} props
 * @param {File|null} props.file
 * @param {string|null} props.previewUrl - Object URL for preview
 * @param {(file: File|null, previewUrl: string|null) => void} props.onFileChange
 * @param {(message: string) => void} [props.onError]
 */
function ImageUploadField({ file, previewUrl, onFileChange, onError }) {
  const inputRef = useRef(null);
  const [isDragging, setIsDragging] = useState(false);

  const validateAndSetFile = useCallback((selectedFile) => {
    if (!selectedFile) return;

    if (!ACCEPTED_TYPES.includes(selectedFile.type)) {
      onError?.('Dozwolone formaty: JPG, PNG, WebP, GIF');
      return;
    }

    if (selectedFile.size > MAX_FILE_SIZE_BYTES) {
      onError?.('Plik jest zbyt duży (maks. 5 MB)');
      return;
    }

    const url = URL.createObjectURL(selectedFile);
    onFileChange(selectedFile, url);
  }, [onFileChange, onError]);

  const handleInputChange = useCallback((e) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) validateAndSetFile(selectedFile);
    e.target.value = '';
  }, [validateAndSetFile]);

  const handleDrop = useCallback((e) => {
    e.preventDefault();
    setIsDragging(false);
    const selectedFile = e.dataTransfer.files?.[0];
    if (selectedFile) validateAndSetFile(selectedFile);
  }, [validateAndSetFile]);

  const handleDragOver = useCallback((e) => {
    e.preventDefault();
    setIsDragging(true);
  }, []);

  const handleDragLeave = useCallback((e) => {
    e.preventDefault();
    setIsDragging(false);
  }, []);

  const handleRemove = useCallback(() => {
    onFileChange(null, null);
  }, [onFileChange]);

  const openFilePicker = useCallback(() => {
    inputRef.current?.click();
  }, []);

  return (
    <div>
      <label className="block text-sm font-medium text-slate-700 mb-1.5">
        Zdjęcie produktu
      </label>

      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        onChange={handleInputChange}
        className="sr-only"
        aria-hidden="true"
      />

      {previewUrl ? (
        <div className="relative inline-block">
          <img
            src={previewUrl}
            alt={file?.name || 'Podgląd zdjęcia produktu'}
            className="w-40 h-40 object-cover rounded-xl border border-gray-200 shadow-sm"
          />
          <button
            type="button"
            onClick={handleRemove}
            className="absolute -top-2 -right-2 p-1.5 bg-red-500 hover:bg-red-600 text-white rounded-full shadow-md transition-colors"
            aria-label="Usuń zdjęcie"
          >
            <X className="w-4 h-4" />
          </button>
          <p className="text-xs text-slate-500 mt-2 max-w-[10rem] truncate">{file?.name}</p>
        </div>
      ) : (
        <div
          role="button"
          tabIndex={0}
          onClick={openFilePicker}
          onKeyDown={(e) => { if (e.key === 'Enter' || e.key === ' ') openFilePicker(); }}
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          className={`border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-all duration-200 ${
            isDragging
              ? 'border-orange-500 bg-orange-50'
              : 'border-gray-300 hover:border-orange-400 hover:bg-orange-50/50'
          }`}
        >
          <div className="flex flex-col items-center gap-3">
            <div className="w-12 h-12 rounded-full bg-orange-100 flex items-center justify-center">
              {isDragging ? (
                <Upload className="w-6 h-6 text-orange-500" />
              ) : (
                <ImageIcon className="w-6 h-6 text-orange-500" />
              )}
            </div>
            <div>
              <p className="text-slate-700 font-medium">
                Przeciągnij zdjęcie lub kliknij, aby wybrać plik
              </p>
              <p className="text-sm text-slate-500 mt-1">
                JPG, PNG, WebP, GIF — maks. 5 MB
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ImageUploadField;
