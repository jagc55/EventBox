import { computed } from "vue";
import { useSession } from "./useSession";
import type { Comment } from "@/services/api";

export function useAuth() {
  const { session, isLoggedIn } = useSession();

  // Verificar si el usuario actual puede eliminar un comentario específico
  const canDeleteComment = computed(() => (comment: Comment) => {
    if (!isLoggedIn.value) return false;

    const currentUserId = session.value.userId;
    const currentUserRole = session.value.roleId;

    // Puede eliminar si es el autor del comentario o si es administrador (roleId = 3)
    return comment.user.id === currentUserId || currentUserRole === 3;
  });

  // Verificar si el usuario es administrador
  const isAdmin = computed(() => {
    return isLoggedIn.value && session.value.roleId === 3;
  });

  // Verificar si el usuario es organizador o administrador
  const isOrganizer = computed(() => {
    return isLoggedIn.value && (session.value.roleId === 2 || session.value.roleId === 3);
  });

  // Obtener el usuario actual
  const currentUser = computed(() => {
    if (!isLoggedIn.value) return null;
    return {
      id: session.value.userId,
      name: session.value.name,
      email: session.value.email,
      roleId: session.value.roleId,
    };
  });

  return {
    session,
    isLoggedIn,
    canDeleteComment,
    isAdmin,
    isOrganizer,
    currentUser,
  };
}
