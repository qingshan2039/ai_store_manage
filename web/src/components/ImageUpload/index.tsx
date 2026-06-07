/* ========================================
   多图上传组件（受控，值为 URL 数组）
   通过通用上传接口 POST /api/files 保存图片，回填返回的 URL。
   ======================================== */
import React, { useState } from 'react';
import { Upload, message, Image } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { UploadFile, UploadProps } from 'antd';
import { fileApi } from '@/api/file';

interface ImageUploadProps {
  value?: string[];
  onChange?: (urls: string[]) => void;
  max?: number;
}

const ImageUpload: React.FC<ImageUploadProps> = ({ value = [], onChange, max = 8 }) => {
  const [previewUrl, setPreviewUrl] = useState('');
  const [previewOpen, setPreviewOpen] = useState(false);

  const fileList: UploadFile[] = value.map((url, idx) => ({
    uid: `${idx}-${url}`,
    name: url.split('/').pop() || `image-${idx}`,
    status: 'done',
    url,
  }));

  const customRequest: UploadProps['customRequest'] = async (options) => {
    const { file, onSuccess, onError } = options;
    try {
      const res = await fileApi.upload(file as File);
      onSuccess?.(res.data);
      onChange?.([...value, res.data.url]);
    } catch (e) {
      onError?.(e as Error);
      message.error('图片上传失败');
    }
  };

  const handleRemove = (f: UploadFile) => {
    onChange?.(value.filter((u) => u !== f.url));
  };

  const handlePreview = (f: UploadFile) => {
    setPreviewUrl(f.url || '');
    setPreviewOpen(true);
  };

  return (
    <>
      <Upload
        listType="picture-card"
        accept="image/*"
        multiple
        fileList={fileList}
        customRequest={customRequest}
        onRemove={handleRemove}
        onPreview={handlePreview}
      >
        {value.length >= max ? null : (
          <div>
            <PlusOutlined />
            <div style={{ marginTop: 8 }}>上传</div>
          </div>
        )}
      </Upload>
      {previewUrl && (
        <Image
          wrapperStyle={{ display: 'none' }}
          preview={{ visible: previewOpen, onVisibleChange: setPreviewOpen, src: previewUrl }}
        />
      )}
    </>
  );
};

export default ImageUpload;
