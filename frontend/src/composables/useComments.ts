import { ref } from "vue";
import { ApiService, type Comment } from "@/services/api";
import { formatDateTime } from "@/utils/dateFormat";

export function useComments(eventId: number) {
  const comments = ref<Comment[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Cargar comentarios del evento
  const loadComments = async () => {
    if (!eventId) return;

    loading.value = true;
    error.value = null;

    try {
      comments.value = await ApiService.getCommentsByEvent(eventId);
    } catch (err) {
      error.value = "Error al cargar los comentarios. Por favor, inténtalo de nuevo.";
      console.error("Error loading comments:", err);
    } finally {
      loading.value = false;
    }
  };

  // Crear un nuevo comentario
  const createComment = async (text: string, userId: number) => {
    if (!text.trim() || !userId) return false;

    try {
      const newComment = await ApiService.createComment({
        description: text.trim(),
        userId,
        eventId,
      });

      // Añadir el comentario al principio de la lista
      comments.value.unshift(newComment);
      return true;
    } catch (err) {
      error.value = "Error al publicar el comentario. Por favor, inténtalo de nuevo.";
      console.error("Error creating comment:", err);
      return false;
    }
  };

  // Eliminar un comentario
  const deleteComment = async (commentId: number) => {
    try {
      await ApiService.deleteComment(commentId);

      // Remover el comentario de la lista
      const index = comments.value.findIndex((c) => c.id === commentId);
      if (index !== -1) {
        comments.value.splice(index, 1);
      }
      return true;
    } catch (err) {
      error.value = "Error al eliminar el comentario. Por favor, inténtalo de nuevo.";
      console.error("Error deleting comment:", err);
      return false;
    }
  };

  // Formatear la fecha del comentario
  const formatCommentDate = (dateString: string): string => {
    try {
      const date = new Date(dateString);
      return formatDateTime(
        date.toISOString().split("T")[0], // fecha YYYY-MM-DD
        date.toTimeString().split(" ")[0] // hora HH:MM:SS
      );
    } catch {
      return dateString;
    }
  };

  return {
    comments,
    loading,
    error,
    loadComments,
    createComment,
    deleteComment,
    formatCommentDate,
  };
}
