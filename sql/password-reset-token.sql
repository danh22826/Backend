USE [QuanLyRapPhim];
GO

IF OBJECT_ID(N'dbo.PasswordResetToken', N'U') IS NULL
BEGIN
    CREATE TABLE [dbo].[PasswordResetToken](
        [Id] [bigint] IDENTITY(1,1) NOT NULL,
        [Token] [varchar](120) NOT NULL,
        [NguoiDungId] [bigint] NOT NULL,
        [CreatedAt] [datetime2](7) NOT NULL,
        [ExpiresAt] [datetime2](7) NOT NULL,
        [UsedAt] [datetime2](7) NULL,
        CONSTRAINT [PK_PasswordResetToken] PRIMARY KEY CLUSTERED ([Id] ASC),
        CONSTRAINT [UQ_PasswordResetToken_Token] UNIQUE NONCLUSTERED ([Token] ASC),
        CONSTRAINT [FK_PasswordResetToken_NguoiDung]
            FOREIGN KEY ([NguoiDungId]) REFERENCES [dbo].[NguoiDung]([id])
    );
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_PasswordResetToken_NguoiDungId'
      AND object_id = OBJECT_ID(N'dbo.PasswordResetToken')
)
BEGIN
    CREATE NONCLUSTERED INDEX [IX_PasswordResetToken_NguoiDungId]
    ON [dbo].[PasswordResetToken]([NguoiDungId] ASC);
END
GO
